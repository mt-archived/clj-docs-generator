
# docs2.api Clojure namespace

##### [README](../../../README.md) > [DOCUMENTATION](../../COVER.md) > docs2.api

### Index

- [create-documentation!](#create-documentation)

### create-documentation!

```
@warning
The create-documentation! function erases the output-dir before printing
the documentation books!
Be careful with configuring this function!
```

```
@param (map) options
{:author (string)(opt)
 :code-dirs (strings in vector)
 :filename-pattern (regex pattern)(opt)
  Default: #"[a-z\_\d]{1,}\.clj[cs]{0,}"
 :lib-name (string)
 :output-dir (string)
 :print-options (keywords in vector)(opt)
  [:code, :credits, :description, :examples, :params, :require, :return, :usages, :warning]
  Default: [:code :credits :description :examples :params :require :return :usages :warning]
 :website (string)(opt)}
```

```
@usage
(create-documentation! {...})
```

```
@usage
(create-documentation! {:author           "Author"
                        :code-dirs        ["submodules/my-repository/source-code"]
                        :filename-pattern "[a-z\-]\.clj"
                        :output-dir       "submodules/my-repository/documentation"
                        :lib-name         "My library"
                        :website          "https://github.com/author/my-repository"})
```

```
@return (string)
```

<details>
<summary>Source code</summary>

```
(defn create-documentation!
  [options]
  (if (v/valid? options {:pattern* core.patterns/OPTIONS-PATTERN})
      (let [options (core.prototypes/options-prototype options)]
           (try                (do (detect.engine/detect-code-files! options)
                    (import.engine/import-code-files! options))
                (catch Exception e (println e))))
```

</details>

<details>
<summary>Require</summary>

```
(ns my-namespace (:require [docs2.api :refer [create-documentation!]]))

(docs2.api/create-documentation! ...)
(create-documentation!           ...)
```

</details>

---

This documentation is generated with the [clj-docs-generator](https://github.com/bithandshake/clj-docs-generator) engine.
