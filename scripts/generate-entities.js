#!/usr/bin/env node
/**
 * Generate JPA entities from kisalu_sandBox.sql dump.
 */
const fs = require("fs");
const path = require("path");

const SQL_FILE = "c:\\Users\\LENOVO\\Downloads\\kisalu_sandBox.sql";
const OUTPUT_DIR =
  "c:\\Users\\LENOVO\\Documents\\drh\\src\\main\\java\\com\\kisalu\\gestion\\drh\\model";
const PACKAGE = "com.kisalu.gestion.drh.model";

const JAVA_RESERVED = new Set([
  "class", "int", "long", "float", "double", "boolean", "char", "byte", "short",
  "void", "null", "true", "false", "new", "return", "if", "else", "for", "while",
  "do", "switch", "case", "default", "break", "continue", "public", "private",
  "protected", "static", "final", "abstract", "interface", "implements", "extends",
  "import", "package", "try", "catch", "finally", "throw", "throws", "this", "super",
  "enum", "goto", "const", "volatile", "synchronized", "transient", "native", "strictfp",
  "find",
]);

function sanitizeName(name) {
  return name.replace(/[\u00A0\u202F\u2007\uFEFF]/g, "").trim();
}

function tableToClass(tableName) {
  return sanitizeName(tableName)
    .split("_")
    .map((p) => p.charAt(0).toUpperCase() + p.slice(1))
    .join("");
}

function columnToField(colName) {
  const clean = sanitizeName(colName);
  const parts = clean.split("_");
  const field =
    parts[0].charAt(0).toLowerCase() +
    parts[0].slice(1).toLowerCase() +
    parts
      .slice(1)
      .map((p) => p.charAt(0).toUpperCase() + p.slice(1).toLowerCase())
      .join("");
  if (JAVA_RESERVED.has(field)) return field + "Value";
  return field;
}

function sqlTypeToJava(sqlType) {
  const t = sqlType.toLowerCase().trim();
  if (t.startsWith("enum(")) return "String";
  if (/^(int|mediumint|smallint|tinyint)/.test(t)) return "Integer";
  if (t.startsWith("bigint")) return "Long";
  if (/^(varchar|char|text|longtext|mediumtext)/.test(t)) return "String";
  if (/^(datetime|timestamp)/.test(t)) return "LocalDateTime";
  if (t.startsWith("date")) return "LocalDate";
  if (t.startsWith("time")) return "LocalTime";
  if (/^(decimal|numeric)/.test(t)) return "BigDecimal";
  if (/^(double|float)/.test(t)) return "Double";
  if (/^(blob|longblob)/.test(t)) return "byte[]";
  return "String";
}

function parseCreateTables(content) {
  const tables = {};
  const pattern = /CREATE TABLE `([^`]+)`\s*\((.*?)\)\s*ENGINE=/gis;
  let m;
  while ((m = pattern.exec(content)) !== null) {
    const tableName = m[1];
    const body = m[2];
    const columns = [];
    for (const line of body.split("\n")) {
      const trimmed = line.trim().replace(/,$/, "");
      if (!trimmed.startsWith("`")) continue;
      const cm = trimmed.match(/^`([^`]+)`\s+(\w+(?:\([^)]*\))?)\s*(.*)/i);
      if (!cm) continue;
      const rest = cm[3].toUpperCase();
      columns.push({
        name: sanitizeName(cm[1]),
        type: cm[2],
        nullable: !rest.includes("NOT NULL"),
        autoIncrement: rest.includes("AUTO_INCREMENT"),
      });
    }
    tables[tableName] = columns;
  }
  return tables;
}

function parsePrimaryKeys(content) {
  const pks = {};
  const pattern = /ALTER TABLE `([^`]+)`\s+ADD PRIMARY KEY \(`([^`]+)`\)/gi;
  let m;
  while ((m = pattern.exec(content)) !== null) {
    pks[m[1]] = sanitizeName(m[2]);
  }
  return pks;
}

function parseForeignKeys(content) {
  const fks = {};
  const alterPattern = /ALTER TABLE `([^`]+)`[\s\S]*?(?=ALTER TABLE `|--\s*Contraintes pour la table `cb_|COMMIT;)/gi;
  const fkPattern =
    /FOREIGN KEY \(`([^`]+)`\) REFERENCES `([^`]+)` \(`([^`]+)`\)(?:\s+ON DELETE (CASCADE|SET NULL))?/gi;

  let block;
  while ((block = alterPattern.exec(content)) !== null) {
    const tableMatch = block[0].match(/^ALTER TABLE `([^`]+)`/);
    if (!tableMatch) continue;
    const table = tableMatch[1];
    if (!block[0].includes("FOREIGN KEY")) continue;

    let fk;
    const localFkPattern = new RegExp(fkPattern.source, "gi");
    while ((fk = localFkPattern.exec(block[0])) !== null) {
      if (!fks[table]) fks[table] = [];
      fks[table].push({
        column: sanitizeName(fk[1]),
        refTable: sanitizeName(fk[2]),
        refColumn: sanitizeName(fk[3]),
        onDelete: (fk[4] || "").toUpperCase(),
      });
    }
  }
  return fks;
}

function relationshipFieldName(colName, refClass, existing) {
  const base = columnToField(colName);
  const candidates = [base];
  if (base.startsWith("id") && base.length > 2) {
    const stripped = base.slice(2);
    candidates.push(stripped.charAt(0).toLowerCase() + stripped.slice(1));
  }
  candidates.push(refClass.charAt(0).toLowerCase() + refClass.slice(1));

  for (const c of candidates) {
    if (!existing.has(c) && !JAVA_RESERVED.has(c)) return c;
  }
  let i = 2;
  while (existing.has(`${base}Ref${i}`)) i++;
  return `${base}Ref${i}`;
}

function generateEntity(tableName, columns, pkCol, fks, classNames) {
  const className = classNames[tableName];
  const fkByCol = Object.fromEntries(fks.map((fk) => [fk.column, fk]));
  const imports = new Set([
    "jakarta.persistence.Entity",
    "jakarta.persistence.Table",
    "jakarta.persistence.Id",
    "jakarta.persistence.Column",
    "jakarta.persistence.ManyToOne",
    "jakarta.persistence.JoinColumn",
    "jakarta.persistence.FetchType",
    "lombok.Getter",
    "lombok.Setter",
    "lombok.NoArgsConstructor",
  ]);
  const fields = [];
  const usedFieldNames = new Set();

  for (const col of columns) {
    const colName = col.name;
    if (fkByCol[colName]) {
      const fk = fkByCol[colName];
      const refClass = classNames[fk.refTable];
      const fieldName = relationshipFieldName(colName, refClass, usedFieldNames);
      usedFieldNames.add(fieldName);

      const joinParts = [`name = "${colName}"`];
      if (fk.refColumn.toLowerCase() !== "id") {
        joinParts.push(`referencedColumnName = "${fk.refColumn}"`);
      }
      if (fk.onDelete === "SET NULL" || col.nullable) {
        joinParts.push("nullable = true");
      }
      const join = joinParts.join(", ");

      fields.push(
        `    @ManyToOne(fetch = FetchType.LAZY)\n` +
          `    @JoinColumn(${join})\n` +
          `    private ${refClass} ${fieldName};`
      );
      imports.add(`${PACKAGE}.${refClass}`);
      continue;
    }

    const javaType = sqlTypeToJava(col.type);
    if (javaType === "LocalDateTime") imports.add("java.time.LocalDateTime");
    else if (javaType === "LocalDate") imports.add("java.time.LocalDate");
    else if (javaType === "LocalTime") imports.add("java.time.LocalTime");
    else if (javaType === "BigDecimal") imports.add("java.math.BigDecimal");

    let fieldName = columnToField(colName);
    if (usedFieldNames.has(fieldName)) fieldName += "Col";
    usedFieldNames.add(fieldName);

    const annotations = [];
    if (colName === pkCol) {
      annotations.push("    @Id");
      if (col.autoIncrement || col.type.toLowerCase().startsWith("int")) {
        annotations.push("    @GeneratedValue(strategy = GenerationType.IDENTITY)");
        imports.add("jakarta.persistence.GeneratedValue");
        imports.add("jakarta.persistence.GenerationType");
      }
    }
    if (colName !== colName.toLowerCase() || colName !== fieldName) {
      const nullablePart = !col.nullable && colName !== pkCol ? ", nullable = false" : "";
      annotations.push(`    @Column(name = "${colName}"${nullablePart})`);
    } else if (!col.nullable && colName !== pkCol) {
      annotations.push("    @Column(nullable = false)");
    }

    const prefix = annotations.length ? annotations.join("\n") + "\n" : "";
    fields.push(`${prefix}    private ${javaType} ${fieldName};`);
  }

  const sortImports = (arr) => arr.sort();
  const javaImports = sortImports([...imports].filter((i) => i.startsWith("java.")));
  const jakartaImports = sortImports([...imports].filter((i) => i.startsWith("jakarta.")));
  const lombokImports = sortImports([...imports].filter((i) => i.startsWith("lombok.")));
  const modelImports = sortImports([...imports].filter((i) => i.startsWith(PACKAGE)));

  const lines = [`package ${PACKAGE};`, ""];
  if (javaImports.length) {
    lines.push(...javaImports.map((i) => `import ${i};`), "");
  }
  if (jakartaImports.length) {
    lines.push(...jakartaImports.map((i) => `import ${i};`), "");
  }
  if (lombokImports.length) {
    lines.push(...lombokImports.map((i) => `import ${i};`), "");
  }
  if (modelImports.length) {
    lines.push(...modelImports.map((i) => `import ${i};`), "");
  }

  lines.push(
    "@Entity",
    `@Table(name = "${tableName}")`,
    "@Getter",
    "@Setter",
    "@NoArgsConstructor",
    `public class ${className} {`,
    "",
    ...fields,
    "",
    "}"
  );
  return lines.join("\n") + "\n";
}

function main() {
  const content = fs.readFileSync(SQL_FILE, "utf8");
  const tables = parseCreateTables(content);
  const pks = parsePrimaryKeys(content);
  const allFks = parseForeignKeys(content);
  const classNames = Object.fromEntries(
    Object.keys(tables).map((t) => [t, tableToClass(t)])
  );

  if (!fs.existsSync(OUTPUT_DIR)) {
    fs.mkdirSync(OUTPUT_DIR, { recursive: true });
  } else {
    for (const f of fs.readdirSync(OUTPUT_DIR)) {
      if (f.endsWith(".java")) fs.unlinkSync(path.join(OUTPUT_DIR, f));
    }
  }

  const tableNames = Object.keys(tables).sort();
  for (const tableName of tableNames) {
    const pk = pks[tableName] || "id";
    const cols = tables[tableName];
    for (const c of cols) {
      if (c.name === pk && c.type.toLowerCase().startsWith("int")) {
        c.autoIncrement = true;
      }
    }
    const entity = generateEntity(
      tableName,
      cols,
      pk,
      allFks[tableName] || [],
      classNames
    );
    const outFile = path.join(OUTPUT_DIR, `${classNames[tableName]}.java`);
    fs.writeFileSync(outFile, entity, "utf8");
    console.log(`Generated ${classNames[tableName]}.java`);
  }
  console.log(`\nTotal: ${tableNames.length} entities`);
}

main();
