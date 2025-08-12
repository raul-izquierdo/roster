package es.uniovi.raul.roster.loader;

import java.util.function.Supplier;

import es.uniovi.raul.roster.loader.formats.*;

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
