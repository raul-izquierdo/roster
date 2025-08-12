# Roster

## Objective

This tool tells you which students should be added, updated, or removed from a GitHub Classroom roster. Since GitHub doesn’t provide an API to manage rosters, you must apply the changes manually in the GitHub Classroom web interface.

The application provides two commands:
- **create** — prints the list of students to add to a roster
- **update** — compares your students list with the existing roster and prints students to add, remove, and those who changed groups

## Quick start

Examples:

```bash
# Create a roster from a students file (prints students to add)
java -jar roster.jar create alumnosMatriculados.xls -f sies -g groups.txt

# Update an existing roster (prints adds, removals, and changes)
java -jar roster.jar update alumnosMatriculados.xls -f sies -r classroom_roster.csv -g groups.txt
```

## Output examples

Example of **update** command:

```bash

## Students to add to the roster

Instructions:
- Go to the Classroom page.
- Click the 'Students' tab.
- Click the 'Update Students' button.
- Select and copy all the lines below at once, then paste them into the 'Create your roster manually' text area.

Izquierdo Castanedo, Raúl (01)
González Pérez, Juan (i02)

## Students to remove from the roster

Instructions:
- Go to the Classroom page.
- Click the 'Students' tab.
- For each of the following lines:
	- Find the student with that roster ID and click the "trash" icon.

Rodríguez, María (01)
Gómez, Ana (i01)

## Students who have changed groups

Instructions:
- Go to the Classroom page.
- Click the 'Students' tab.
- For each of the following lines:
	- Find the student using the old roster ID (shown on the left side of the arrow) and click the "pen" icon.
	- Replace the old roster ID with the new one (shown on the right side of the arrow).

González, Juan (02) ---> González Pérez, Juan (03)
Valles, Pedro (i01) ---> Valle, Pedro (i02)
Ramírez, Lucía (01) ---> Ramírez, Lucía (02)
```

## Usage

Syntax:

```bash
java -jar roster.jar <command> [OPTIONS] [<students-file>]
```

Commands:
- **create** — prints the students to add (based on the students file, optionally filtered by groups)
- **update*** — prints students to add, to remove, and who changed groups (requires the roster CSV)

Options:
- **students-file**: The file containing the students and their groups. (default: "alumnosMatriculados.xls")
- **-f <format>**: The format of the students file. Supported: "excel", "csv", "sies". (default: "sies")
- **-r <roster.csv>**: The roster CSV exported from GitHub Classroom (used only with the 'update' command). (default: "classroom_roster.csv")
- **-g <groups.txt>**: (optional) A file with the teacher’s groups. Only students in these groups will be included. If not specified, all students are included.
- **-h, --help**: Show help.

## Students file formats

### SIES format

This is the format used by the SIES tool (https://sies.uniovi.es/serviciosdocentes) to export student data. It is used at the University of Oviedo.

### CSV format

CSV file with two columns, no header row:
- Student name — use quotes if the name contains commas or special characters.
- Group — any text identifying the group.

Example:

```csv
"Izquierdo Castanedo, Raúl", 01
"González, Juan", i02
```

### Excel format

Same as the CSV format (two columns, no header row).

## Groups file format

This file lists the teacher’s groups. Only students in these groups will be included in the output.

Requirements:
- Plain text file: one group ID per line.
- Blank lines are ignored.

```
01
02
i01
```

CSV files with more than one column are allowed, but only the first column is considered. For example, the same `schedule.csv` file used in the [show_solutions](https://github.com/raul-izquierdo/show_solutions) tool can also be used here:

```
01, monday, 10:00
02, tuesday, 11:00
i01, wednesday, 12:00
```

## License

MIT License

Copyright (c) 2025 Raul Izquierdo Castanedo
