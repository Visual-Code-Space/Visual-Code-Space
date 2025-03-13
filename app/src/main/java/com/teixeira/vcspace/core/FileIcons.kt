/*
 * This file is part of Visual Code Space.
 *
 * Visual Code Space is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Visual Code Space is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Visual Code Space.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package com.teixeira.vcspace.core

import com.teixeira.vcspace.extensions.toFile

data class FileIcon(
    val name: String,
    val fileExtensions: List<String> = emptyList(),
    val fileNames: List<String> = emptyList(),
    val light: Boolean = false
)

data class FolderIcon(
    val name: String,
    val folderNames: List<String> = emptyList(),
    val light: Boolean = false
)

object FileIcons {
    private val defaultFileIcon = FileIcon("file")
    private val defaultFolderIcon = FolderIcon("folder")

    private val fileIcons = listOf(
        FileIcon("html", fileExtensions = listOf("html", "htm", "xhtml", "html_vm", "asp")),
        FileIcon(
            name = "json",
            fileExtensions = listOf(
                "json", "json5", "jsonc", "jsonl", "tsbuildinfo", "ndjson"
            ),
            fileNames = listOf(
                ".jscsrc",
                ".jshintrc",
                "composer.lock",
                ".jsbeautifyrc",
                ".esformatter",
                "cdp.pid",
                ".lintstagedrc",
                ".whitesource"
            )
        ),
        FileIcon("markdown", fileExtensions = listOf("md", "markdown", "rst")),
        FileIcon("blink", fileExtensions = listOf("blink"), light = true),
        FileIcon("css", fileExtensions = listOf("css")),
        FileIcon("sass", fileExtensions = listOf("sass", "scss")),
        FileIcon("less", fileExtensions = listOf("less")),
        FileIcon("hjson", fileExtensions = listOf("hjson")),
        FileIcon("just", fileNames = listOf("justfile", ".justfile")),
        FileIcon(
            name = "pug",
            fileExtensions = listOf("jade", "pug"),
            fileNames = listOf(".pug-lintrc", ".pug-lintrc.js", ".pug-lintrc.json")
        ),
        FileIcon(
            name = "jinja",
            fileExtensions = listOf("jinja", "jinja2", "j2", "jinja-html"),
            light = true
        ),
        FileIcon("proto", fileExtensions = listOf("proto")),
        FileIcon(
            name = "playwright",
            fileNames = listOf(
                "playwright.config.js",
                "playwright.config.mjs",
                "playwright.config.ts",
                "playwright.config.base.js",
                "playwright.config.base.mjs",
                "playwright.config.base.ts",
                "playwright-ct.config.js",
                "playwright-ct.config.mjs",
                "playwright-ct.config.ts"
            )
        ),
        FileIcon("sublime", fileExtensions = listOf("sublime-project", "sublime-workspace")),
        FileIcon("simulink", fileExtensions = listOf("slx")),
        FileIcon("twine", fileExtensions = listOf("tw", "twee")),
        FileIcon(
            name = "yaml",
            fileExtensions = listOf("yml.dist", "yaml.dist", "YAML-tmLanguage", "yml", "yaml")
        ),
        FileIcon(
            name = "xml",
            fileExtensions = listOf(
                "xml",
                "plist",
                "xsd",
                "dtd",
                "xsl",
                "xslt",
                "resx",
                "iml",
                "xquery",
                "tmLanguage",
                "manifest",
                "project",
                "xml.dist",
                "xml.dist.sample",
                "dmn",
                "jrxml",
                "xmp",
            ),
            fileNames = listOf(".htaccess")
        ),
        FileIcon("toml", fileExtensions = listOf("toml"), light = true),
        FileIcon(
            name = "image",
            fileExtensions = listOf(
                "png",
                "jpeg",
                "jpg",
                "gif",
                "ico",
                "tif",
                "tiff",
                "psd",
                "psb",
                "ami",
                "apx",
                "avif",
                "bmp",
                "bpg",
                "brk",
                "cur",
                "dds",
                "exr",
                "fpx",
                "gbr",
                "img",
                "jbig2",
                "jb2",
                "jng",
                "jxr",
                "pgf",
                "pic",
                "raw",
                "webp",
                "eps",
                "afphoto",
                "ase",
                "aseprite",
                "clip",
                "cpt",
                "heif",
                "heic",
                "kra",
                "mdp",
                "ora",
                "pdn",
                "reb",
                "sai",
                "tga",
                "xcf",
                "jfif",
                "ppm",
                "pbm",
                "pgm",
                "pnm",
                "icns",
                "3fr",
                "ari",
                "arw",
                "bay",
                "braw",
                "crw",
                "cr2",
                "cr3",
                "cap",
                "data",
                "dcs",
                "dcr",
                "dng",
                "drf",
                "eip",
                "erf",
                "fff",
                "gpr",
                "iiq",
                "k25",
                "kdc",
                "mdc",
                "mef",
                "mos",
                "mrw",
                "nef",
                "nrw",
                "obm",
                "orf",
                "pef",
                "ptx",
                "pxn",
                "r3d",
                "raf",
                "rwl",
                "rw2",
                "rwz",
                "sr2",
                "srf",
                "srw",
                "x3f",
            )
        ),
        FileIcon("palette", fileExtensions = listOf("pal", "gpl", "act")),
        FileIcon("javascript", fileExtensions = listOf("esx", "mjs", "js")),
        FileIcon("react", fileExtensions = listOf("jsx")),
        FileIcon("react_ts", fileExtensions = listOf("tsx")),
        FileIcon(
            name = "rocket",
            fileNames = listOf(
                ".release-it.json",
                ".release-it.ts",
                ".release-it.js",
                ".release-it.cjs",
                ".release-it.yaml",
                ".release-it.yml",
                ".release-it.toml",
                "release.toml",
                "release-plz.toml",
                ".release-plz.toml",
            )
        ),
        FileIcon(
            name = "routing",
            fileExtensions = listOf(
                "routing.ts",
                "routing.tsx",
                "routing.js",
                "routing.jsx",
                "routes.ts",
                "routes.tsx",
                "routes.js",
                "routes.jsx",
            ),
            fileNames = listOf(
                "router.js",
                "router.jsx",
                "router.ts",
                "router.tsx",
                "routes.js",
                "routes.jsx",
                "routes.ts",
                "routes.tsx",
            )
        ),
        FileIcon(
            name = "settings",
            fileExtensions = listOf(
                "ini",
                "dlc",
                "config",
                "conf",
                "properties",
                "prop",
                "settings",
                "option",
                "props",
                "prefs",
                "sln.dotsettings",
                "sln.dotsettings.user",
                "cfg",
                "cnf",
                "tool-versions",
            ),
            fileNames = listOf(
                ".jshintignore",
                ".buildignore",
                ".mrconfig",
                ".yardopts",
                "manifest.mf",
                ".clang-format",
                ".clang-format-ignore",
                ".clang-tidy",
                ".conf",
                "compile_flags.txt",
            )
        ),
        FileIcon("typescript-def", fileExtensions = listOf("d.ts", "d.cts", "d.mts")),
        FileIcon("pdf", fileExtensions = listOf("pdf")),
        FileIcon(
            name = "table",
            fileExtensions = listOf("xlsx", "xlsm", "xls", "csv", "tsv", "psv", "ods")
        ),
        FileIcon(
            name = "vscode",
            fileExtensions = listOf(
                "vscodeignore",
                "vsixmanifest",
                "vsix",
                "code-workplace",
                "code-workspace",
                "code-profile",
                "code-snippets",
            )
        ),
        FileIcon(
            name = "visualstudio",
            fileExtensions = listOf(
                "csproj",
                "ruleset",
                "sln",
                "slnx",
                "suo",
                "vb",
                "vbs",
                "vcxitems",
                "vcxitems.filters",
                "vcxproj",
                "vcxproj.filters",
            )
        ),
        FileIcon(
            name = "database",
            fileExtensions = listOf(
                "pdb",
                "sql",
                "pks",
                "pkb",
                "accdb",
                "mdb",
                "sqlite",
                "sqlite3",
                "pgsql",
                "postgres",
                "plpgsql",
                "psql",
                "db",
                "db3",
                "dblite",
                "dblite3",
                "debugsymbols",
                "odb",
            )
        ),
        FileIcon("csharp", fileExtensions = listOf("cs", "csx", "csharp")),
        FileIcon("qsharp", fileExtensions = listOf("qs")),
        FileIcon(
            name = "zip",
            fileExtensions = listOf(
                "zip",
                "z",
                "tar",
                "gz",
                "xz",
                "lz",
                "liz",
                "lzma",
                "lzma2",
                "lz4",
                "lz5",
                "lzh",
                "lha",
                "br",
                "bz2",
                "bzip2",
                "gzip",
                "brotli",
                "7z",
                "001",
                "rar",
                "far",
                "tz",
                "taz",
                "tlz",
                "txz",
                "tgz",
                "tpz",
                "tbz",
                "tbz2",
                "zst",
                "zstd",
                "tzst",
                "tzstd",
                "cab",
                "cpio",
                "rpm",
                "deb",
                "arj",
                "wim",
                "swm",
                "esd",
                "fat",
                "xar",
                "ntfs",
                "hfs",
                "squashfs",
                "apfs",
            )
        ),
        FileIcon("exe", fileExtensions = listOf("exe", "msi")),
        FileIcon("hex", fileExtensions = listOf("dat", "bin", "hex")),
        FileIcon("java", fileExtensions = listOf("java", "jsp")),
        FileIcon("jar", fileExtensions = listOf("jar")),
        FileIcon("javaclass", fileExtensions = listOf("class")),
        FileIcon("c3", fileExtensions = listOf("c3")),
        FileIcon("c", fileExtensions = listOf("c", "i", "mi")),
        FileIcon("h", fileExtensions = listOf("h")),
        FileIcon("cpp", fileExtensions = listOf("cc", "cpp", "cxx", "c++", "cp", "mii", "ii")),
        FileIcon("hpp", fileExtensions = listOf("hh", "hpp", "hxx", "h++", "hp", "tcc", "inl")),
        FileIcon("rc", fileExtensions = listOf("rc")),
        FileIcon("go", fileExtensions = listOf("go")),
        FileIcon("go-mod", fileExtensions = listOf("go.mod", "go.sum", "go.work", "go.work.sum")),
        FileIcon("python", fileExtensions = listOf("py")),
        FileIcon(
            name = "python-misc",
            fileExtensions = listOf("pyc", "whl"),
            fileNames = listOf(
                "requirements.txt",
                "pipfile",
                ".python-version",
                "manifest.in",
                "pylintrc",
                ".pylintrc",
                "pyproject.toml",
                "py.typed",
            )
        ),
        FileIcon("url", fileExtensions = listOf("url")),
        FileIcon(
            name = "console",
            fileExtensions = listOf(
                "sh",
                "ksh",
                "csh",
                "tcsh",
                "zsh",
                "bash",
                "bat",
                "cmd",
                "awk",
                "fish",
                "exp",
                "nu",
            ),
            fileNames = listOf(
                "commit-msg", "pre-commit", "pre-push", "post-merge"
            )
        ),
        FileIcon(
            name = "powershell",
            fileExtensions = listOf("ps1", "psm1", "psd1", "ps1xml", "psc1", "pssc")
        ),
        FileIcon(
            name = "gradle",
            fileExtensions = listOf("gradle"),
            fileNames = listOf("gradle.properties", "gradlew", "gradle-wrapper.properties")
        ),
        FileIcon("word", fileExtensions = listOf("doc", "docx", "rtf", "odt")),
        FileIcon(
            name = "certificate",
            fileExtensions = listOf("cer", "cert", "crt"),
            fileNames = listOf(
                "copying",
                "copying.md",
                "copying.rst",
                "copying.txt",
                "copyright",
                "copyright.md",
                "copyright.rst",
                "copyright.txt",
                "license",
                "license-agpl",
                "license-apache",
                "license-bsd",
                "license-mit",
                "license-gpl",
                "license-lgpl",
                "license.md",
                "license.rst",
                "license.txt",
                "licence",
                "licence-agpl",
                "licence-apache",
                "licence-bsd",
                "licence-mit",
                "licence-gpl",
                "licence-lgpl",
                "licence.md",
                "licence.rst",
                "licence.txt",
                "unlicense",
                "unlicense.txt",
            )
        ),
        FileIcon(
            name = "key",
            fileExtensions = listOf(
                "pub",
                "key",
                "pem",
                "asc",
                "gpg",
                "passwd",
                "shasum",
                "sha256",
                "sha256sum",
                "sha256sums",
            ),
            fileNames = listOf(".htpasswd", "sha256sums", ".secrets")
        ),
        FileIcon(
            name = "font",
            fileExtensions = listOf(
                "woff",
                "woff2",
                "ttf",
                "eot",
                "suit",
                "otf",
                "bmap",
                "fnt",
                "odttf",
                "ttc",
                "font",
                "fonts",
                "sui",
                "ntf",
                "mrf",
            )
        ),
        FileIcon("lib", fileExtensions = listOf("lib", "a", "bib")),
        FileIcon("dll", fileExtensions = listOf("dll", "ilk", "so")),
        FileIcon(
            name = "ruby",
            fileExtensions = listOf("rb", "erb", "rbs"),
            fileNames = listOf(".ruby-version")
        ),
        FileIcon("fsharp", fileExtensions = listOf("fs", "fsx", "fsi", "fsproj")),
        FileIcon("swift", fileExtensions = listOf("swift")),
        FileIcon("arduino", fileExtensions = listOf("ino")),
        FileIcon(
            name = "docker",
            fileExtensions = listOf(
                "dockerignore",
                "dockerfile",
                "docker-compose.yml",
                "docker-compose.yaml",
                "containerignore",
                "containerfile",
                "compose.yaml",
                "compose.yml",
            ),
            fileNames = listOf(
                "dockerfile",
                "dockerfile.prod",
                "dockerfile.production",
                "dockerfile.alpha",
                "dockerfile.beta",
                "dockerfile.stage",
                "dockerfile.staging",
                "dockerfile.dev",
                "dockerfile.development",
                "dockerfile.local",
                "dockerfile.test",
                "dockerfile.testing",
                "dockerfile.ci",
                "dockerfile.web",
                "dockerfile.windows",
                "dockerfile.worker",

                "docker-compose.yml",
                "docker-compose.override.yml",
                "docker-compose.prod.yml",
                "docker-compose.production.yml",
                "docker-compose.alpha.yml",
                "docker-compose.beta.yml",
                "docker-compose.stage.yml",
                "docker-compose.staging.yml",
                "docker-compose.dev.yml",
                "docker-compose.development.yml",
                "docker-compose.local.yml",
                "docker-compose.test.yml",
                "docker-compose.testing.yml",
                "docker-compose.ci.yml",
                "docker-compose.web.yml",
                "docker-compose.worker.yml",

                "docker-compose.yaml",
                "docker-compose.override.yaml",
                "docker-compose.prod.yaml",
                "docker-compose.production.yaml",
                "docker-compose.alpha.yaml",
                "docker-compose.beta.yaml",
                "docker-compose.stage.yaml",
                "docker-compose.staging.yaml",
                "docker-compose.dev.yaml",
                "docker-compose.development.yaml",
                "docker-compose.local.yaml",
                "docker-compose.test.yaml",
                "docker-compose.testing.yaml",
                "docker-compose.ci.yaml",
                "docker-compose.web.yaml",
                "docker-compose.worker.yaml",

                "containerfile",
                "containerfile.prod",
                "containerfile.production",
                "containerfile.alpha",
                "containerfile.beta",
                "containerfile.stage",
                "containerfile.staging",
                "containerfile.dev",
                "containerfile.development",
                "containerfile.local",
                "containerfile.test",
                "containerfile.testing",
                "containerfile.ci",
                "containerfile.web",
                "containerfile.worker",

                "compose.yaml",
                "compose.override.yaml",
                "compose.prod.yaml",
                "compose.production.yaml",
                "compose.alpha.yaml",
                "compose.beta.yaml",
                "compose.stage.yaml",
                "compose.staging.yaml",
                "compose.dev.yaml",
                "compose.development.yaml",
                "compose.local.yaml",
                "compose.test.yaml",
                "compose.testing.yaml",
                "compose.ci.yaml",
                "compose.web.yaml",
                "compose.worker.yaml",

                "compose.yml",
                "compose.override.yml",
                "compose.prod.yml",
                "compose.production.yml",
                "compose.alpha.yml",
                "compose.beta.yml",
                "compose.stage.yml",
                "compose.staging.yml",
                "compose.dev.yml",
                "compose.development.yml",
                "compose.local.yml",
                "compose.test.yml",
                "compose.testing.yml",
                "compose.ci.yml",
                "compose.web.yml",
                "compose.worker.yml",
            )
        ),
        FileIcon(
            name = "video",
            fileExtensions = listOf(
                "webm",
                "mkv",
                "flv",
                "vob",
                "ogv",
                "ogg",
                "gifv",
                "avi",
                "mov",
                "qt",
                "wmv",
                "yuv",
                "rm",
                "rmvb",
                "mp4",
                "m4v",
                "mpg",
                "mp2",
                "mpeg",
                "mpe",
                "mpv",
                "m2v",
            )
        ),
        FileIcon(
            name = "audio",
            fileExtensions = listOf(
                "8svx",
                "aa",
                "aac",
                "aax",
                "ac3",
                "aif",
                "aiff",
                "alac",
                "amr",
                "ape",
                "caf",
                "cda",
                "cdr",
                "dss",
                "ec3",
                "efs",
                "enc",
                "flac",
                "flp",
                "gp",
                "gsm",
                "it",
                "m3u",
                "m3u8",
                "m4a",
                "m4b",
                "m4p",
                "m4r",
                "mid",
                "mka",
                "mmf",
                "mod",
                "mp3",
                "mpc",
                "mscz",
                "mtm",
                "mui",
                "musx",
                "mxl",
                "nsa",
                "opus",
                "pkf",
                "qcp",
                "ra",
                "rf64",
                "rip",
                "sdt",
                "sesx",
                "sf2",
                "stap",
                "tg",
                "voc",
                "vqf",
                "wav",
                "weba",
                "wfp",
                "wma",
                "wpl",
                "wproj",
                "wv",
            )
        ),
        FileIcon("rust", fileExtensions = listOf("rs", "ron")),
        FileIcon("xaml", fileExtensions = listOf("xaml")),
        FileIcon("haskell", fileExtensions = listOf("hs", "lhs")),
        FileIcon("kotlin", fileExtensions = listOf("kt", "kts")),
        FileIcon(
            name = "git",
            fileExtensions = listOf("patch"),
            fileNames = listOf(
                ".git",
                ".gitignore",
                ".gitmessage",
                ".gitignore-global",
                ".gitignore_global",
                ".gitattributes",
                ".gitattributes-global",
                ".gitattributes_global",
                ".gitconfig",
                ".gitmodules",
                ".gitkeep",
                ".keep",
                ".gitpreserve",
                ".gitinclude",
                ".git-blame-ignore",
                ".git-blame-ignore-revs",
                ".git-for-windows-updater",
                "git-history",
            )
        ),
        FileIcon("lua", fileExtensions = listOf("lua"), fileNames = listOf(".luacheckrc")),
        FileIcon("clojure", fileExtensions = listOf("clj", "cljs", "cljc")),
        FileIcon("groovy", fileExtensions = listOf("groovy")),
        FileIcon("r", fileExtensions = listOf("r", "rmd"), fileNames = listOf(".Rhistory")),
        FileIcon("dart", fileExtensions = listOf("dart"), fileNames = listOf(".pubignore")),
        FileIcon("dart_generated", fileExtensions = listOf("freezed.dart", "g.dart")),
        FileIcon(
            name = "cmake",
            fileExtensions = listOf("cmake"),
            fileNames = listOf("cmakelists.txt", "cmakecache.txt")
        ),
        FileIcon(
            name = "assembly",
            fileExtensions = listOf(
                "asm",
                "a51",
                "inc",
                "nasm",
                "s",
                "ms",
                "agc",
                "ags",
                "aea",
                "argus",
                "mitigus",
                "binsource",
            )
        ),
        FileIcon("vue", fileExtensions = listOf("vue")),
        FileIcon("semgrep", fileNames = listOf("semgrep.yml", ".semgrepignore")),
        FileIcon(
            name = "vue-config",
            fileNames = listOf(
                "vue.config.js",
                "vue.config.ts",
                "vetur.config.js",
                "vetur.config.ts",
                "volar.config.js",
            )
        ),
        FileIcon(
            name = "nuxt",
            fileNames = listOf("nuxt.config.js", "nuxt.config.ts", ".nuxtignore", ".nuxtrc")
        ),
        FileIcon(
            name = "javascript-map",
            fileExtensions = listOf("js.map", "mjs.map", "cjs.map")
        ),
        FileIcon("css-map", fileExtensions = listOf("css.map")),
    )

    private val folderIcons = listOf(
        FolderIcon("folder-rust", folderNames = listOf("rust")),
        FolderIcon("folder-robot", folderNames = listOf("bot", "bots", "robot", "robots")),
        FolderIcon("folder-src", folderNames = listOf("src", "srcs", "source", "sources", "code")),
        FolderIcon(
            "folder-dist",
            folderNames = listOf(
                "dist",
                "out",
                "output",
                "build",
                "builds",
                "release",
                "bin",
                "distribution"
            )
        ),
        FolderIcon(
            "folder-css",
            folderNames = listOf("css", "stylesheet", "stylesheets", "style", "styles")
        ),
        FolderIcon("folder-sass", folderNames = listOf("sass", "scss")),
        FolderIcon("folder-television", folderNames = listOf("tv", "television")),
        FolderIcon("folder-desktop", folderNames = listOf("desktop", "display")),
        FolderIcon("folder-console", folderNames = listOf("console")),
        FolderIcon(
            "folder-images",
            folderNames = listOf(
                "images",
                "image",
                "imgs",
                "img",
                "icons",
                "icon",
                "icos",
                "ico",
                "figures",
                "figure",
                "figs",
                "fig",
                "screenshot",
                "screenshots",
                "screengrab",
                "screengrabs",
                "pic",
                "pics",
                "picture",
                "pictures",
                "photo",
                "photos",
                "photograph",
                "photographs"
            )
        ),
        FolderIcon("folder-scripts", folderNames = listOf("script", "scripts", "scripting")),
        FolderIcon("folder-node", folderNames = listOf("node", "nodejs", "node_modules")),
        FolderIcon("folder-javascript", folderNames = listOf("js", "javascript", "javascripts")),
        FolderIcon("folder-json", folderNames = listOf("json", "jsons")),
        FolderIcon("folder-font", folderNames = listOf("font", "fonts")),
        FolderIcon("folder-bower", folderNames = listOf("bower_components")),
        FolderIcon(
            "folder-test",
            folderNames = listOf("test", "tests", "testing", "snapshots", "spec", "specs")
        ),
        FolderIcon("folder-directive", folderNames = listOf("directive", "directives")),
        FolderIcon("folder-jinja", folderNames = listOf("jinja", "jinja2", "j2"), light = true),
        FolderIcon("folder-markdown", folderNames = listOf("markdown", "md")),
        FolderIcon("folder-pdm", folderNames = listOf("pdm-plugins", "pdm-build")),
        FolderIcon("folder-php", folderNames = listOf("php")),
        FolderIcon("folder-phpmailer", folderNames = listOf("phpmailer")),
        FolderIcon("folder-sublime", folderNames = listOf("sublime")),
        FolderIcon(
            "folder-docs",
            folderNames = listOf(
                "doc",
                "docs",
                "document",
                "documents",
                "documentation",
                "post",
                "posts",
                "article",
                "articles",
                "wiki",
                "news"
            )
        ),
        FolderIcon("folder-gh-workflows", folderNames = listOf("github/workflows")),
        FolderIcon("folder-git", folderNames = listOf("git", "patches", "githooks", "submodules")),
        FolderIcon("folder-github", folderNames = listOf("github")),
        FolderIcon("folder-gitea", folderNames = listOf("gitea")),
        FolderIcon("folder-gitlab", folderNames = listOf("gitlab")),
        FolderIcon("folder-forgejo", folderNames = listOf("forgejo")),
        FolderIcon("folder-vscode", folderNames = listOf("vscode", "vscode-test")),
        FolderIcon(
            "folder-views",
            folderNames = listOf(
                "view",
                "views",
                "screen",
                "screens",
                "page",
                "pages",
                "public_html",
                "html"
            )
        ),
        FolderIcon("folder-vue", folderNames = listOf("vue")),
        FolderIcon("folder-vuepress", folderNames = listOf("vuepress")),
        FolderIcon("folder-expo", folderNames = listOf("expo", "expo-shared")),
        FolderIcon(
            "folder-config",
            folderNames = listOf(
                "cfg",
                "cfgs",
                "conf",
                "confs",
                "config",
                "configs",
                "configuration",
                "configurations",
                "setting",
                "settings",
                "META-INF",
                "option",
                "options",
                "pref",
                "prefs",
                "preference",
                "preferences"
            )
        ),
        FolderIcon(
            "folder-i18n",
            folderNames = listOf(
                "i18n",
                "internationalization",
                "lang",
                "langs",
                "language",
                "languages",
                "locale",
                "locales",
                "l10n",
                "localization",
                "translation",
                "translate",
                "translations",
                "tx"
            )
        ),
        FolderIcon(
            "folder-components",
            folderNames = listOf("components", "widget", "widgets", "fragments")
        ),
        FolderIcon("folder-verdaccio", folderNames = listOf("verdaccio")),
        FolderIcon("folder-aurelia", folderNames = listOf("aurelia_project")),
        FolderIcon(
            "folder-resource",
            folderNames = listOf(
                "resource",
                "resources",
                "res",
                "asset",
                "assets",
                "static",
                "report",
                "reports"
            )
        ),
        FolderIcon(
            "folder-lib",
            folderNames = listOf(
                "lib",
                "libs",
                "library",
                "libraries",
                "vendor",
                "vendors",
                "third-party",
                "lib64"
            )
        ),
        FolderIcon(
            "folder-theme",
            folderNames = listOf("themes", "theme", "color", "colors", "design", "designs")
        ),
        FolderIcon("folder-webpack", folderNames = listOf("webpack")),
        FolderIcon("folder-global", folderNames = listOf("global")),
        FolderIcon(
            "folder-public",
            folderNames = listOf(
                "public",
                "www",
                "wwwroot",
                "web",
                "website",
                "websites",
                "site",
                "browser",
                "browsers"
            )
        ),
        FolderIcon(
            "folder-include",
            folderNames = listOf("inc", "include", "includes", "partial", "partials", "inc64")
        ),
        FolderIcon("folder-docker", folderNames = listOf("docker", "dockerfiles", "dockerhub")),
        FolderIcon("folder-ngrx-store", folderNames = listOf("store")),
        FolderIcon("folder-ngrx-effects", folderNames = listOf("effects")),
        FolderIcon("folder-ngrx-state", folderNames = listOf("states", "state")),
        FolderIcon("folder-ngrx-reducer", folderNames = listOf("reducers", "reducer")),
        FolderIcon("folder-ngrx-actions", folderNames = listOf("actions")),
        FolderIcon("folder-ngrx-entities", folderNames = listOf("entities")),
        FolderIcon("folder-ngrx-selectors", folderNames = listOf("selectors")),
        FolderIcon("folder-redux-reducer", folderNames = listOf("reducers", "reducer")),
        FolderIcon("folder-redux-actions", folderNames = listOf("actions")),
        FolderIcon("folder-redux-selector", folderNames = listOf("selectors", "selector")),
        FolderIcon("folder-redux-store", folderNames = listOf("store", "stores")),
        FolderIcon(
            "folder-react-components",
            folderNames = listOf("components", "react", "jsx", "reactjs", "react-components")
        ),
        FolderIcon("folder-astro", folderNames = listOf("astro")),
        FolderIcon(
            "folder-database",
            folderNames = listOf("db", "data", "database", "databases", "sql")
        ),
        FolderIcon("folder-log", folderNames = listOf("log", "logs", "logging")),
        FolderIcon("folder-target", folderNames = listOf("target")),
        FolderIcon("folder-temp", folderNames = listOf("temp", "tmp", "cached", "cache")),
        FolderIcon("folder-aws", folderNames = listOf("aws", "azure", "gcp")),
        FolderIcon(
            "folder-audio",
            folderNames = listOf(
                "aud",
                "auds",
                "audio",
                "audios",
                "music",
                "sound",
                "sounds",
                "voice",
                "voices",
                "recordings"
            )
        ),
        FolderIcon(
            "folder-video",
            folderNames = listOf("vid", "vids", "video", "videos", "movie", "movies", "media")
        ),
        FolderIcon("folder-kubernetes", folderNames = listOf("kubernetes", "k8s")),
        FolderIcon("folder-import", folderNames = listOf("import", "imports", "imported")),
        FolderIcon("folder-export", folderNames = listOf("export", "exports", "exported")),
        FolderIcon("folder-wakatime", folderNames = listOf("wakatime")),
        FolderIcon("folder-circleci", folderNames = listOf("circleci")),
        FolderIcon("folder-wordpress", folderNames = listOf("wordpress-org", "wp-content")),
        FolderIcon("folder-gradle", folderNames = listOf("gradle")),
        FolderIcon(
            "folder-coverage",
            folderNames = listOf(
                "coverage",
                "nyc-output",
                "nyc_output",
                "e2e",
                "it",
                "integration-test",
                "integration-tests"
            )
        ),
        FolderIcon(
            "folder-class",
            folderNames = listOf("class", "classes", "model", "models", "schemas", "schema")
        ),
        FolderIcon(
            "folder-other",
            folderNames = listOf(
                "other",
                "others",
                "misc",
                "miscellaneous",
                "extra",
                "extras",
                "etc"
            )
        ),
        FolderIcon("folder-lua", folderNames = listOf("lua")),
        FolderIcon("folder-turborepo", folderNames = listOf("turbo")),
        FolderIcon(
            "folder-typescript",
            folderNames = listOf("typescript", "ts", "typings", "@types", "types")
        ),
        FolderIcon("folder-graphql", folderNames = listOf("graphql", "gql")),
        FolderIcon("folder-routes", folderNames = listOf("routes", "router", "routers")),
        FolderIcon("folder-ci", folderNames = listOf("ci")),
        FolderIcon(
            "folder-benchmark",
            folderNames = listOf(
                "benchmark",
                "benchmarks",
                "performance",
                "profiling",
                "measure",
                "measures",
                "measurement"
            )
        ),
        FolderIcon(
            "folder-messages",
            folderNames = listOf(
                "messages",
                "messaging",
                "forum",
                "chat",
                "chats",
                "conversation",
                "conversations",
                "dialog",
                "dialogs"
            )
        ),
        FolderIcon("folder-less", folderNames = listOf("less")),
        FolderIcon(
            "folder-gulp",
            folderNames = listOf(
                "gulp",
                "gulp-tasks",
                "gulpfile.js",
                "gulpfile.mjs",
                "gulpfile.ts",
                "gulpfile.babel.js"
            )
        ),
        FolderIcon("folder-python", folderNames = listOf("python", "pycache", "pytest_cache")),
        FolderIcon("folder-sandbox", folderNames = listOf("sandbox", "playground")),
        FolderIcon("folder-scons", folderNames = listOf("scons", "sconf_temp", "scons_cache")),
        FolderIcon("folder-mojo", folderNames = listOf("mojo")),
        FolderIcon("folder-moon", folderNames = listOf("moon")),
        FolderIcon("folder-debug", folderNames = listOf("debug", "debugger", "debugging")),
        FolderIcon("folder-fastlane", folderNames = listOf("fastlane")),
        FolderIcon(
            "folder-plugin",
            folderNames = listOf(
                "plugin",
                "plugins",
                "mod",
                "mods",
                "modding",
                "extension",
                "extensions",
                "addon",
                "addons",
                "addin",
                "addins",
                "module",
                "modules"
            )
        ),
        FolderIcon("folder-middleware", folderNames = listOf("middleware", "middlewares")),
        FolderIcon(
            "folder-controller",
            folderNames = listOf(
                "controller",
                "controllers",
                "controls",
                "service",
                "services",
                "provider",
                "providers",
                "handler",
                "handlers"
            )
        ),
        FolderIcon("folder-ansible", folderNames = listOf("ansible")),
        FolderIcon(
            "folder-server",
            folderNames = listOf("server", "servers", "backend", "backends")
        ),
        FolderIcon(
            "folder-client",
            folderNames = listOf("client", "clients", "frontend", "frontends", "pwa", "spa")
        ),
        FolderIcon("folder-tasks", folderNames = listOf("tasks", "tickets")),
        FolderIcon("folder-android", folderNames = listOf("android")),
        FolderIcon("folder-ios", folderNames = listOf("ios")),
        FolderIcon("folder-ui", folderNames = listOf("presentation", "gui", "ui", "ux")),
        FolderIcon("folder-upload", folderNames = listOf("uploads", "upload")),
        FolderIcon(
            "folder-download",
            folderNames = listOf("downloads", "download", "downloader", "downloaders")
        ),
        FolderIcon(
            "folder-tools",
            folderNames = listOf(
                "tools",
                "toolkit",
                "toolkits",
                "toolbox",
                "toolboxes",
                "tooling",
                "devtools",
                "kit",
                "kits"
            )
        ),
        FolderIcon("folder-helper", folderNames = listOf("helpers", "helper")),
        FolderIcon("folder-serverless", folderNames = listOf("serverless")),
        FolderIcon("folder-api", folderNames = listOf("api", "apis", "restapi")),
        FolderIcon(
            "folder-app",
            folderNames = listOf("app", "apps", "application", "applications")
        ),
        FolderIcon(
            "folder-apollo",
            folderNames = listOf("apollo", "apollo-client", "apollo-cache", "apollo-config")
        ),
        FolderIcon(
            "folder-archive",
            folderNames = listOf(
                "arc",
                "arcs",
                "archive",
                "archives",
                "archival",
                "bkp",
                "bkps",
                "bak",
                "baks",
                "backup",
                "backups",
                "back-up",
                "back-ups",
                "history",
                "histories"
            )
        ),
        FolderIcon("folder-batch", folderNames = listOf("batch", "batchs", "batches")),
        FolderIcon("folder-buildkite", folderNames = listOf("buildkite")),
        FolderIcon("folder-cluster", folderNames = listOf("cluster", "clusters")),
        FolderIcon(
            "folder-command",
            folderNames = listOf("command", "commands", "commandline", "cmd", "cli", "clis")
        ),
        FolderIcon("folder-constant", folderNames = listOf("constant", "constants")),
        FolderIcon(
            "folder-container",
            folderNames = listOf("container", "containers", "devcontainer")
        ),
        FolderIcon("folder-content", folderNames = listOf("content", "contents")),
        FolderIcon("folder-context", folderNames = listOf("context", "contexts")),
        FolderIcon("folder-core", folderNames = listOf("core")),
        FolderIcon("folder-delta", folderNames = listOf("delta", "deltas", "changes")),
        FolderIcon("folder-dump", folderNames = listOf("dump", "dumps")),
        FolderIcon(
            "folder-examples",
            folderNames = listOf(
                "demo",
                "demos",
                "example",
                "examples",
                "sample",
                "samples",
                "sample-data"
            )
        ),
        FolderIcon(
            "folder-environment",
            folderNames = listOf("env", "envs", "environment", "environments", "venv")
        ),
        FolderIcon(
            "folder-functions",
            folderNames = listOf(
                "func",
                "funcs",
                "function",
                "functions",
                "lambda",
                "lambdas",
                "logic",
                "math",
                "maths",
                "calc",
                "calcs",
                "calculation",
                "calculations"
            )
        ),
        FolderIcon(
            "folder-generator",
            folderNames = listOf(
                "generator",
                "generators",
                "generated",
                "cfn-gen",
                "gen",
                "gens",
                "auto"
            )
        ),
        FolderIcon("folder-hook", folderNames = listOf("hook", "hooks", "trigger", "triggers")),
        FolderIcon("folder-job", folderNames = listOf("job", "jobs")),
        FolderIcon(
            "folder-keys",
            folderNames = listOf("key", "keys", "token", "tokens", "jwt", "secret", "secrets")
        ),
        FolderIcon("folder-layout", folderNames = listOf("layout", "layouts")),
        FolderIcon(
            "folder-mail",
            folderNames = listOf("mail", "mails", "email", "emails", "smtp", "mailers")
        ),
        FolderIcon("folder-mappings", folderNames = listOf("mappings", "mapping")),
        FolderIcon("folder-meta", folderNames = listOf("meta")),
        FolderIcon("folder-changesets", folderNames = listOf("changesets", "changeset")),
        FolderIcon(
            "folder-packages",
            folderNames = listOf(
                "package",
                "packages",
                "pkg",
                "pkgs",
                "serverpackages",
                "devpackages",
                "dependencies"
            )
        ),
        FolderIcon("folder-shared", folderNames = listOf("shared", "common")),
        FolderIcon("folder-shader", folderNames = listOf("glsl", "hlsl", "shader", "shaders")),
        FolderIcon("folder-stack", folderNames = listOf("stack", "stacks")),
        FolderIcon(
            "folder-template",
            folderNames = listOf(
                "template",
                "templates",
                "github/ISSUE_TEMPLATE",
                "github/PULL_REQUEST_TEMPLATE"
            )
        ),
        FolderIcon("folder-utils", folderNames = listOf("util", "utils", "utility", "utilities")),
        FolderIcon("folder-supabase", folderNames = listOf("supabase")),
        FolderIcon("folder-private", folderNames = listOf("private")),
        FolderIcon("folder-linux", folderNames = listOf("linux", "linuxbsd", "unix")),
        FolderIcon("folder-windows", folderNames = listOf("windows", "win", "win32")),
        FolderIcon("folder-macos", folderNames = listOf("macos", "mac", "osx", "DS_Store")),
        FolderIcon(
            "folder-error",
            folderNames = listOf("error", "errors", "err", "errs", "crash", "crashes")
        ),
        FolderIcon("folder-event", folderNames = listOf("event", "events")),
        FolderIcon(
            "folder-secure",
            folderNames = listOf(
                "auth",
                "authentication",
                "secure",
                "security",
                "cert",
                "certs",
                "certificate",
                "certificates",
                "ssl",
                "cipher",
                "cypher",
                "tls"
            )
        ),
        FolderIcon("folder-custom", folderNames = listOf("custom", "customs")),
        FolderIcon(
            "folder-mock",
            folderNames = listOf(
                "draft",
                "drafts",
                "mock",
                "mocks",
                "fixture",
                "fixtures",
                "concept",
                "concepts",
                "sketch",
                "sketches"
            )
        ),
        FolderIcon(
            "folder-syntax",
            folderNames = listOf("syntax", "syntaxes", "spellcheck", "spellcheckers")
        ),
        FolderIcon("folder-vm", folderNames = listOf("vm", "vms")),
        FolderIcon("folder-stylus", folderNames = listOf("stylus")),
        FolderIcon("folder-flow", folderNames = listOf("flow-typed")),
        FolderIcon(
            "folder-rules",
            folderNames = listOf(
                "rule",
                "rules",
                "validation",
                "validations",
                "validator",
                "validators"
            )
        ),
        FolderIcon(
            "folder-review",
            folderNames = listOf(
                "review",
                "reviews",
                "revisal",
                "revisals",
                "reviewed",
                "preview",
                "previews"
            )
        ),
        FolderIcon(
            "folder-animation",
            folderNames = listOf(
                "anim",
                "anims",
                "animation",
                "animations",
                "animated",
                "motion",
                "motions",
                "transition",
                "transitions",
                "easing",
                "easings"
            )
        ),
        FolderIcon("folder-guard", folderNames = listOf("guard", "guards")),
        FolderIcon("folder-prisma", folderNames = listOf("prisma", "prisma/schema")),
        FolderIcon("folder-pipe", folderNames = listOf("pipe", "pipes", "pipeline", "pipelines")),
        FolderIcon("folder-svg", folderNames = listOf("svg", "svgs")),
        FolderIcon("folder-nuxt", folderNames = listOf("nuxt")),
        FolderIcon("folder-terraform", folderNames = listOf("terraform")),
        FolderIcon(
            "folder-mobile",
            folderNames = listOf("mobile", "mobiles", "portable", "portability", "phone", "phones")
        ),
        FolderIcon("folder-stencil", folderNames = listOf("stencil")),
        FolderIcon("folder-firebase", folderNames = listOf("firebase")),
        FolderIcon("folder-svelte", folderNames = listOf("svelte", "svelte-kit")),
        FolderIcon(
            "folder-update",
            folderNames = listOf("update", "updates", "upgrade", "upgrades")
        ),
        FolderIcon("folder-intellij", folderNames = listOf("idea"), light = true),
        FolderIcon(
            "folder-azure-pipelines",
            folderNames = listOf("azure-pipelines", "azure-pipelines-ci")
        ),
        FolderIcon("folder-mjml", folderNames = listOf("mjml")),
        FolderIcon(
            "folder-admin",
            folderNames = listOf(
                "admin",
                "admins",
                "manager",
                "managers",
                "moderator",
                "moderators"
            )
        ),
        FolderIcon(
            "folder-jupyter",
            folderNames = listOf("jupyter", "notebook", "notebooks", "ipynb")
        ),
        FolderIcon("folder-scala", folderNames = listOf("scala")),
        FolderIcon(
            "folder-connection",
            folderNames = listOf(
                "connection",
                "connections",
                "integration",
                "integrations",
                "remote",
                "remotes"
            )
        ),
        FolderIcon("folder-quasar", folderNames = listOf("quasar")),
        FolderIcon("folder-next", folderNames = listOf("next")),
        FolderIcon("folder-cobol", folderNames = listOf("cobol")),
        FolderIcon("folder-yarn", folderNames = listOf("yarn")),
        FolderIcon("folder-husky", folderNames = listOf("husky")),
        FolderIcon("folder-storybook", folderNames = listOf("storybook", "stories")),
        FolderIcon("folder-base", folderNames = listOf("base", "bases")),
        FolderIcon(
            "folder-cart",
            folderNames = listOf("cart", "shopping-cart", "shopping", "shop")
        ),
        FolderIcon("folder-home", folderNames = listOf("home", "start", "main", "landing")),
        FolderIcon("folder-project", folderNames = listOf("project", "projects")),
        FolderIcon("folder-interface", folderNames = listOf("interface", "interfaces")),
        FolderIcon("folder-netlify", folderNames = listOf("netlify")),
        FolderIcon("folder-enum", folderNames = listOf("enum", "enums")),
        FolderIcon(
            "folder-contract",
            folderNames = listOf(
                "pact",
                "pacts",
                "contract",
                "contracts",
                "contract-testing",
                "contract-test",
                "contract-tests"
            )
        ),
        FolderIcon("folder-helm", folderNames = listOf("helm", "helmchart", "helmcharts")),
        FolderIcon("folder-queue", folderNames = listOf("queue", "queues", "bull", "mq")),
        FolderIcon("folder-vercel", folderNames = listOf("vercel", "now")),
        FolderIcon("folder-cypress", folderNames = listOf("cypress")),
        FolderIcon("folder-decorators", folderNames = listOf("decorator", "decorators")),
        FolderIcon("folder-java", folderNames = listOf("java")),
        FolderIcon("folder-resolver", folderNames = listOf("resolver", "resolvers")),
        FolderIcon("folder-angular", folderNames = listOf("angular")),
        FolderIcon("folder-unity", folderNames = listOf("unity")),
        FolderIcon("folder-pdf", folderNames = listOf("pdf", "pdfs")),
        FolderIcon(
            "folder-proto",
            folderNames = listOf("protobuf", "protobufs", "proto", "protos")
        ),
        FolderIcon("folder-plastic", folderNames = listOf("plastic")),
        FolderIcon("folder-gamemaker", folderNames = listOf("gamemaker", "gamemaker2")),
        FolderIcon("folder-mercurial", folderNames = listOf("hg", "hghooks", "hgext")),
        FolderIcon("folder-godot", folderNames = listOf("godot", "godot-cpp")),
        FolderIcon("folder-lottie", folderNames = listOf("lottie", "lotties", "lottiefiles")),
        FolderIcon("folder-taskfile", folderNames = listOf("taskfile", "taskfiles")),
        FolderIcon("folder-drizzle", folderNames = listOf("drizzle")),
        FolderIcon("folder-cloudflare", folderNames = listOf("cloudflare")),
        FolderIcon("folder-seeders", folderNames = listOf("seeds", "seeders", "seed", "seeding")),
        FolderIcon("folder-bicep", folderNames = listOf("bicep")),
        FolderIcon("folder-snapcraft", folderNames = listOf("snap", "snapcraft")),
        FolderIcon("folder-development", folderNames = listOf("dev", "development")),
        FolderIcon("folder-flutter", folderNames = listOf("flutter")),
        FolderIcon("folder-snippet", folderNames = listOf("snippet", "snippets")),
        FolderIcon("folder-element", folderNames = listOf("element", "elements")),
        FolderIcon("folder-src-tauri", folderNames = listOf("src-tauri")),
        FolderIcon("folder-favicon", folderNames = listOf("favicon", "favicons")),
        FolderIcon("folder-lefthook", folderNames = listOf("lefthook", "lefthook-local")),
        FolderIcon("folder-bloc", folderNames = listOf("bloc", "cubit", "blocs", "cubits")),
        FolderIcon("folder-powershell", folderNames = listOf("powershell", "ps", "ps1")),
        FolderIcon(
            "folder-repository",
            folderNames = listOf("repository", "repositories", "repo", "repos")
        ),
        FolderIcon("folder-luau", folderNames = listOf("luau")),
        FolderIcon("folder-obsidian", folderNames = listOf("obsidian")),
        FolderIcon("folder-trash", folderNames = listOf("trash")),
        FolderIcon("folder-cline", folderNames = listOf("cline_docs")),
        FolderIcon("folder-liquibase", folderNames = listOf("liquibase")),
        FolderIcon("folder-dart", folderNames = listOf("dart", "dart_tool", "dart_tools")),
        FolderIcon("folder-zeabur", folderNames = listOf("zeabur"))
    )

    fun getSvgIconForFile(filePath: String, isLight: Boolean = false): String {
        val file = filePath.toFile()
        val fileName = file.name.lowercase()
        val extension = fileName.substringAfter(".", "").lowercase()

        val icon = fileIcons.find { fileName in it.fileNames } ?: fileIcons.find {
            extension in it.fileExtensions
        } ?: defaultFileIcon

        return if (icon.light && isLight) {
            "files/icons/${icon.name}_light.svg"
        } else {
            "files/icons/${icon.name}.svg"
        }
    }

    fun getSvgIconForFolder(
        folderPath: String,
        isExpanded: Boolean,
        isLight: Boolean = false
    ): String {
        val folder = folderPath.toFile()
        val folderName = folder.name.lowercase().replaceFirst(".", "")

        val icon = folderIcons.find {
            folderName in it.folderNames
        } ?: defaultFolderIcon

        return if (icon.light && isLight) {
            "files/icons/${icon.name}${if (isExpanded) "-open" else ""}_light.svg"
        } else {
            "files/icons/${icon.name}${if (isExpanded) "-open" else ""}.svg"
        }
    }
}
