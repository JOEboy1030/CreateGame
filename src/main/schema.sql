CREATE TABLE IF NOT EXISTS dictionary_words (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    jmdict_entry_id INTEGER NOT NULL,
    surface TEXT NOT NULL,
    reading TEXT NOT NULL,
    UNIQUE (
        jmdict_entry_id,
        surface,
        reading
    )
);

CREATE INDEX IF NOT EXISTS
    idx_dictionary_word_lookup
ON dictionary_words (
    surface,
    reading
);