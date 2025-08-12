# Roster

## Objective

This tool tells you which students should be _added_, _changed_, or _removed_ from a GitHub Classroom roster. Since GitHub does not provide an API to manage rosters, you must make these changes manually in the GitHub Classroom web interface.

Example usage:
```bash
java -jar roster.jar
```

Example output:
```bash

## Students to add (copy all of them together and paste into the roster web interface)

Izquierdo Castanedo, Raúl-01
Gonzalez Perez, Juan-i02

## Students to remove (remove them one by one using the trash icon)

Rodriguez, Maria-01
Gomez, Ana-i01

## Students to change group (change them one by one using the pencil icon)

Gonzalez, Juan-02 -> Gonzalez Perez, Juan-03
Valles, Pedro-i01 -> Valle, Pedro-i02
Ramirez, Lucia-01 -> Ramirez, Lucía-02
```

## Running the Program

Run the program with the following command:

```bash
java -jar roster.jar
```

This is equivalent to:

```bash
java -jar roster.jar alumnosMatriculados.xls -f sies -r classroom_roster.csv -g groups.csv
```

Syntax:
```bash
java -jar roster.jar [<students-file>] [-f <format>] [-r <roster.csv>] [-g <groups.csv>]
```

Options:
- **students-file**: The file containing the students and their groups. (default: "alumnosMatriculados.xls")
- **-f <format>**: The format of the students file. Supported formats: "excel", "csv", and "sies". (default: "sies")
- **-r <roster.csv>**: The file downloaded from GitHub Classroom with the student IDs. (default: "classroom_roster.csv"). If this file is not provided or does not exist, all students will be considered **new students**.
- **-g <groups.csv>**: The file containing the teacher's groups. Only students in these groups will be included in the output. If not specified, all students will be included. (default: "groups.csv")
- **-h, --help**: Show this help message.

## Formats of the Students File

### SIES Format

This is the format used by the SIES tool (https://sies.uniovi.es/serviciosdocentes) to export student data. It is proprietary to the University of Oviedo and is only useful for teachers in this context.

### CSV Format

A CSV file with the following columns:
- **Student name**: No specific format is required, but use quotes if the name contains commas or other special characters.
- **Group**: The group assigned to the student. No specific format is required.

There should be **no header row**. Example:
```csv
"Izquierdo Castanedo, Raúl", 01
"Gonzalez, Juan", i02
...
```

### Excel Format

This format is the same as the CSV format. There should be **no header row**.

## Format of the Groups File

This file contains the groups that are assigned to the teacher. Only students in these groups will be included in the output. If this file is not provided, all students will be included.

Requirements:
- Plain text file, one group ID per line.
- Blank lines are ignored.

```
01
02
i01
```

CSV files with more than one column are allowed, but only the first column is considered. For example, the same _schedule.csv_ file used in the [show_solutions](https://github.com/raul-izquierdo/show_solutions) tool can also be used here:
```
01, monday, 10:00
02, tuesday, 11:00
i01, wednesday, 12:00
```

## License

MIT License

Copyright (c) 2025 Raul Izquierdo Castanedo
