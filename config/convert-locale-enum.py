filepath = "../src/main/resources/multiverse-core_en.properties"

enumList = []

with open(filepath) as f:
    for line in f:
        line = line.strip()
        if len(line) == 0:
            enumList.append("")
        elif line.startswith("#"):
            enumList.append(line.replace("#", "//"))
        else:
            parts = line.split("=")
            if (len(parts) < 2):
                continue
            parts = parts[0].split(".")[1:]
            enumList.append("_".join([p.upper() for p in parts]) + ",")

with open("out.txt", "w") as f:
    f.write("\n".join(enumList))