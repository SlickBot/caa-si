# Keep readable stack traces in release crash reports.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Moshi bundles consumer rules to keep the generated adapters and class names.
# Moshi models are codegen-only -> @JsonClass(generateAdapter = true).
