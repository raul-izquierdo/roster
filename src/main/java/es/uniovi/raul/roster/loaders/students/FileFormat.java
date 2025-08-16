package es.uniovi.raul.roster.loaders.students;

import java.util.function.Supplier;

import es.uniovi.raul.roster.loaders.students.formats.*;

public enum FileFormat {

    EXCEL(ExcelLoader::new),
    CSV(CsvLoader::new),
    SIES(SiesLoader::new);

    private final Supplier<FormatLoader> supplier;

    FileFormat(Supplier<FormatLoader> supplier) {
        this.supplier = supplier;
    }

    FormatLoader createFormatLoader() {
        return supplier.get();
    }
}
