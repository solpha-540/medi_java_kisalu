#!/usr/bin/env node
/**
 * Remplace Lombok par getters, setters et constructeurs explicites.
 */
const fs = require("fs");
const path = require("path");

const SRC = path.join(__dirname, "..", "src", "main", "java");

function walk(dir, files = []) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) walk(full, files);
    else if (entry.name.endsWith(".java") && entry.name !== "package-info.java") files.push(full);
  }
  return files;
}

function capitalize(s) {
  return s.charAt(0).toUpperCase() + s.slice(1);
}

function getterName(type, field) {
  if (type === "boolean") return `is${capitalize(field)}`;
  return `get${capitalize(field)}`;
}

function parseClassAnnotations(beforeClass) {
  return {
    getter: /@Getter\b/.test(beforeClass),
    setter: /@Setter\b/.test(beforeClass),
    data: /@Data\b/.test(beforeClass),
    noArgs: /@NoArgsConstructor\b/.test(beforeClass),
    allArgs: /@AllArgsConstructor\b/.test(beforeClass),
    requiredArgs: /@RequiredArgsConstructor\b/.test(beforeClass),
  };
}

function parseFields(body) {
  const fields = [];
  const lines = body.split("\n");
  for (let i = 0; i < lines.length; i++) {
    const line = lines[i];
    if (/^\s+@Getter\s*$/.test(line) && i + 1 < lines.length) {
      const next = lines[i + 1];
      const m = next.match(/^\s+private\s+(?:final\s+)?([\w.<>,\s\[\]]+?)\s+(\w+)\s*;/);
      if (m) {
        fields.push({ type: m[1].trim(), name: m[2], final: /final/.test(next), getterOnly: true });
        i++;
        continue;
      }
    }
    const m = line.match(/^\s+private\s+(final\s+)?([\w.<>,\s\[\]]+?)\s+(\w+)\s*;/);
    if (m) {
      fields.push({ type: m[2].trim(), name: m[3], final: !!m[1], getterOnly: false });
    }
  }
  return fields;
}

function generateMethods(className, fields, ann) {
  const wantGetter = ann.getter || ann.data || ann.getter;
  const wantSetter = ann.setter || ann.data;
  const lines = [];

  if (ann.noArgs || ann.data || ann.getter || ann.setter) {
    lines.push(`    public ${className}() {`, "    }", "");
  }

  if (ann.allArgs || ann.data) {
    const params = fields.map((f) => `${f.type} ${f.name}`).join(", ");
    lines.push(`    public ${className}(${params}) {`);
    for (const f of fields) lines.push(`        this.${f.name} = ${f.name};`);
    lines.push("    }", "");
  }

  if (ann.requiredArgs) {
    const finalFields = fields.filter((f) => f.final);
    if (finalFields.length) {
      const params = finalFields.map((f) => `${f.type} ${f.name}`).join(", ");
      lines.push(`    public ${className}(${params}) {`);
      for (const f of finalFields) lines.push(`        this.${f.name} = ${f.name};`);
      lines.push("    }", "");
    }
  }

  for (const f of fields) {
    const gName = getterName(f.type, f.name);
    if (wantGetter || f.getterOnly) {
      lines.push(`    public ${f.type} ${gName}() {`, `        return ${f.name};`, "    }", "");
    }
    if (wantSetter && !f.final && !f.getterOnly) {
      lines.push(
        `    public void set${capitalize(f.name)}(${f.type} ${f.name}) {`,
        `        this.${f.name} = ${f.name};`,
        "    }",
        ""
      );
    }
  }

  return lines.join("\n");
}

function processFile(filePath) {
  let content = fs.readFileSync(filePath, "utf8");
  if (!content.includes("lombok")) return false;

  const classMatch = content.match(
    /([\s\S]*?)((?:@\w+(?:\([^)]*\))?\s*\n)*)(public\s+(?:class|record|enum)\s+(\w+)(?:<[^>]+>)?\s*\{)([\s\S]*)(\n\})/
  );
  if (!classMatch) return false;

  let before = classMatch[1];
  let classAnns = classMatch[2];
  const classDecl = classMatch[3];
  const className = classMatch[4];
  let body = classMatch[5];

  const ann = parseClassAnnotations(classAnns);
  if (!ann.getter && !ann.setter && !ann.data && !ann.noArgs && !ann.allArgs && !ann.requiredArgs) {
    if (!body.includes("@Getter") && !body.includes("@Setter")) return false;
    ann.getter = body.includes("@Getter");
    ann.setter = body.includes("@Setter");
    ann.noArgs = classAnns.includes("@NoArgsConstructor");
  }
  if (ann.data) {
    ann.getter = true;
    ann.setter = true;
    ann.noArgs = true;
    ann.allArgs = true;
  }

  body = body.replace(/^\s+@Getter\s*$\n/gm, "");
  body = body.replace(/^\s+@Setter\s*$\n/gm, "");

  const fields = parseFields(body);
  const methods = generateMethods(className, fields, ann);

  classAnns = classAnns
    .replace(/^\s*@Getter\s*$\n?/gm, "")
    .replace(/^\s*@Setter\s*$\n?/gm, "")
    .replace(/^\s*@Data\s*$\n?/gm, "")
    .replace(/^\s*@Builder\s*$\n?/gm, "")
    .replace(/^\s*@NoArgsConstructor\s*$\n?/gm, "")
    .replace(/^\s*@AllArgsConstructor\s*$\n?/gm, "")
    .replace(/^\s*@RequiredArgsConstructor\s*$\n?/gm, "");

  before = before.replace(/^import lombok\.[^\n]+\n/gm, "");

  const trimmedBody = body.trimEnd();
  const newContent =
    before +
    classAnns +
    classDecl +
    "\n" +
    trimmedBody +
    "\n\n" +
    methods +
    "}\n";

  fs.writeFileSync(filePath, newContent, "utf8");
  return true;
}

const files = walk(SRC);
let count = 0;
for (const f of files) {
  if (processFile(f)) {
    count++;
    console.log("Updated:", path.relative(SRC, f));
  }
}
console.log(`\nTotal: ${count} fichiers`);
