
(ns docs-generator.process.engine
    (:require [docs-generator.import.state  :as import.state]
              [docs-generator.process.env   :as process.env]
              [docs-generator.process.state :as process.state]
              [docs-generator.process.utils :as process.utils]
              [docs-generator.read.state    :as read.state]
              [fruits.map.api               :as map]
              [fruits.string.api            :as string]
              [fruits.vector.api            :as vector]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn process-function-description
  ; @ignore
  ;
  ; @param (map) options
  ; @param (string) layer-name
  ; @param (string) api-filepath
  ; @param (map) function-data
  ;
  ; @usage
  ; (process-function-description {...} "clj" "my-repository/source-code/my_directory/api.clj" {...})
  ;
  ; @return (string)
  [_ _ _ function-data]
  (if-let [description (get-in function-data ["header" "description"])]
          (str "@description" description)))

(defn process-function-warning
  ; @ignore
  ;
  ; @param (map) options
  ; @param (string) layer-name
  ; @param (string) api-filepath
  ; @param (map) function-data
  ;
  ; @usage
  ; (process-function-warning {...} "clj" "my-repository/source-code/my_directory/api.clj" {...})
  ;
  ; @return (string)
  [_ _ _ function-data]
  (if-let [warning (get-in function-data ["header" "warning"])]
          (str "@warning" warning)))

(defn process-function-params
  ; @ignore
  ;
  ; @param (map) options
  ; @param (string) layer-name
  ; @param (string) api-filepath
  ; @param (map) function-data
  ; {"header"
  ;   {"params" (map)(opt)
  ;     {"name" (string)
  ;     {"optional?" (boolean)
  ;     {"types" (string)}}}
  ;
  ; @example
  ; (process-function-params {...} "clj" "my-repository/source-code/my_directory/api.clj" {...})
  ; =>
  ; ["@param (string)(opt) my-param"]
  ;
  ; @return (strings in vector)
  [_ _ _ function-data]
  (if-let [params (get-in function-data ["header" "params"])]
          (letfn [(f0 [params param]
                      (let [name      (get param "name")
                            optional? (get param "optional?")
                            types     (get param "types")
                            sample    (get param "sample")]
                           (conj params (str "@param ("types")"
                                             (if optional? "(opt)")
                                             " "name
                                             (if sample (str "\n"sample))))))]
                 (reduce f0 [] params))))

(defn process-function-usages
  ; @ignore
  ;
  ; @param (map) options
  ; @param (string) layer-name
  ; @param (string) api-filepath
  ; @param (map) function-data
  ; {"header"
  ;   {"usages" (maps in vector)(opt)
  ;     [{"call" (string)}]}}
  ;
  ; @example
  ; (process-function-usages {...} "clj" "my-repository/source-code/my_directory/api.clj" {...})
  ; =>
  ; ["\n@usage ..."]
  ;
  ; @return (strings in vector)
  [_ _ _ function-data]
  (if-let [usages (get-in function-data ["header" "usages"])]
          (letfn [(f0 [usages usage]
                      (let [call (get usage "call")]
                           (conj usages (str "\n@usage"call))))]
                 (reduce f0 [] usages))))

(defn process-function-examples
  ; @ignore
  ;
  ; @param (map) options
  ; @param (string) layer-name
  ; @param (string) api-filepath
  ; @param (map) function-data
  ; {"header"
  ;   {"examples" (maps in vector)(opt)
  ;     [{"call" (string)}]}}
  ;
  ; @example
  ; (process-function-examples {...} "clj" "my-repository/source-code/my_directory/api.clj" {...})
  ; =>
  ; ["\n@example ..."]
  ;
  ; @return (?)
  [_ _ _ function-data]
  (if-let [examples (get-in function-data ["header" "examples"])]
          (letfn [(f0 [examples example]
                      (let [call   (get example "call")
                            result (get example "result")]
                           (conj examples (str "\n@example"call"=>"result))))]
                 (reduce f0 [] examples))))

(defn process-function-return
  ; @ignore
  ;
  ; @param (map) options
  ; @param (string) layer-name
  ; @param (string) api-filepath
  ; @param (map) function-data
  ; {"header"
  ;   {"return" (map)(opt)
  ;     {"sample" (string)(opt)
  ;      "types" (string)(opt)}}}
  ;
  ; @example
  ; (process-function-return {...} "clj" "my-repository/source-code/my_directory/pi.clj" {...})
  ; =>
  ; "@return(string)"
  ;
  ; @return (string)
  [_ _ _ function-data]
  (if-let [types (get-in function-data ["header" "return" "types"])]
          (if-let [sample (get-in function-data ["header" "return" "sample"])]
                  (str "@return ("types")"sample)
                  (str "@return ("types")"))))

(defn process-function-header
  ; @ignore
  ;
  ; @param (map) options
  ; @param (string) layer-name
  ; @param (string) api-filepath
  ; @param (map) function-data
  ;
  ; @example
  ; (process-function-header {...} "clj" "my-repository/source-code/my_directory/api.clj" {...})
  ; =>
  ; (?)
  ;
  ; @return (map)
  ; {"description" (string)
  ;  "examples" (strings in vector)
  ;  "params" (strings in vector)
  ;  "return" (string)
  ;  "usages" (strings in vector)
  ;  "warning" (string)}
  [options layer-name api-filepath function-data]
  {"description" (process-function-description options layer-name api-filepath function-data)
   "examples"    (process-function-examples    options layer-name api-filepath function-data)
   "params"      (process-function-params      options layer-name api-filepath function-data)
   "return"      (process-function-return      options layer-name api-filepath function-data)
   "usages"      (process-function-usages      options layer-name api-filepath function-data)
   "warning"     (process-function-warning     options layer-name api-filepath function-data)})

(defn process-function
  ; @ignore
  ;
  ; @param (map) options
  ; @param (string) layer-name
  ; @param (string) api-filepath
  ; @param (map) function-data
  ;
  ; @example
  ; (process-function {...} "clj" "my-repository/source-code/my_directory/api.clj" {...})
  ; =>
  ; (?)
  ;
  ; @return (map)
  ; {"header" (map)
  ;  "name" (string)}
  [options layer-name api-filepath function-data]
  {"header" (process-function-header options layer-name api-filepath function-data)
   "code"   (get function-data "code")
   "name"   (get function-data "name")})

(defn process-api-functions
  ; @ignore
  ;
  ; @param (map) options
  ; @param (string) layer-name
  ; @param (string) api-filepath
  ;
  ; @example
  ; (process-api-functions {...} "clj" "my-repository/source-code/my_directory/api.clj")
  ; =>
  ; {}
  ;
  ; @return (map)
  [options layer-name api-filepath]
  (let [functions (get-in @read.state/LAYERS [layer-name api-filepath "functions"])]
       (letfn [(f0 [result function-data]
                   (conj result (process-function options layer-name api-filepath function-data)))]
              (reduce f0 [] functions))))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn process-api-links
  ; @ignore
  ;
  ; @param (map) options
  ; @param (string) layer-name
  ; @param (string) api-filepath
  ;
  ; @return (map)
  [options layer-name api-filepath]
  (let [functions (get-in @read.state/LAYERS [layer-name api-filepath "functions"])
        constants (get-in @read.state/LAYERS [layer-name api-filepath "constants"])]
       (letfn [(f0 [])])))
              ;(reduce-kv f0 {} layers))))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn process-api-file
  ; @ignore
  ;
  ; @param (map) options
  ; @param (string) layer-name
  ; @param (string) api-filepath
  ;
  ; @example
  ; (process-api-file {...} "clj" "my-repository/source-code/my_directory/api.clj")
  ; =>
  ; {}
  ;
  ; @return (map)
  ; {}
  [options layer-name api-filepath]
  {"links"     (process-api-links     options layer-name api-filepath)
   "functions" (process-api-functions options layer-name api-filepath)})
  ;"constants" (process-api-constants options layer-name api-filepath)

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn process-layer
  ; @ignore
  ;
  ; @param (map) options
  ; @param (string) layer-name
  ;
  ; @example
  ; (process-layer {...} "clj")
  ; =>
  ; {"my-repository/source-code/my_directory/api.clj" {...}
  ;  "..." {...}}
  ;
  ; @return (map)
  [options layer-name]
  (let [layer-data (get @read.state/LAYERS layer-name)]
       (letfn [(f0 [layer-data api-filepath api-data]
                   (assoc layer-data api-filepath (process-api-file options layer-name api-filepath)))]
              (reduce-kv f0 {} layer-data))))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn process-layers!
  ; @ignore
  ;
  ; @param (map) options
  [options]
  (let [layers @read.state/LAYERS]
       (letfn [(f0 [_ layer-name _]
                   (let [layer-data (process-layer options layer-name)]
                        (swap! process.state/LAYERS assoc layer-name layer-data)))]
              (reduce-kv f0 nil layers))))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn process-layer-links
  ; @ignore
  ;
  ; @param (map) options
  ;  {:output-dir (string)}
  ; @param (string) layer-name
  ; @param (strings in vector) links
  ;
  ; @return (strings in vector)
  [{:keys [output-dir] :as options} layer-name links]
  (let [layer-data (get @read.state/LAYERS layer-name)
        api-files  (map/keys layer-data)]
       ; Az f0 függvény vizsgálja, hogy az api fájlban van-e bármilyen átirányitás
       ; ha nincs akkor nem készül hozzá API.md fájl ezért a linket sem szükséges betenni a COVER.md fájlba
       (letfn [(f0 [api-filepath] (or (vector/not-empty? (get-in @read.state/LAYERS [layer-name api-filepath "functions"]))
                                      (vector/not-empty? (get-in @read.state/LAYERS [layer-name api-filepath "constants"]))))
               (f1 [links api-filepath]
                   (let [api-namespace (get-in @import.state/LAYERS [layer-name api-filepath "namespace"])
                         md-path   (process.utils/md-path options layer-name api-filepath)
                         rel-path  (-> md-path (string/not-start-with output-dir)
                                               (string/not-start-with "/"))]
                        (if (f0 api-filepath)
                            (update links layer-name vector/conj-item (str "* ["api-namespace"]("rel-path"/API.md)"))
                            (->     links))))]
              (reduce f1 links (vector/abc-items api-files)))))

(defn process-cover-links
  ; @ignore
  ;
  ; @param (map) options
  ;
  ; @return (map)
  ; {"clj" (strings in vector)
  ;  "cljc" (strings in vector)
  ;  "cljs" (strings in vector)}
  [options]
  (let [layers @read.state/LAYERS]
       (letfn [(f0 [links layer-name _]
                   (process-layer-links options layer-name links))]
              (reduce-kv f0 {} layers))))

(defn process-cover-title
  ; @ignore
  ;
  ; @param (map) options
  ; {:lib-name (string)}
  ;
  ; @usage
  ; (process-subtitle {...})
  ;
  ; @return (string)
  [{:keys [lib-name]}]
  (str "### "lib-name))

(defn process-cover
  ; @ignore
  ;
  ; @param (map) options
  ;
  ; @return (map)
  [options]
  {"links" (process-cover-links options)
   "title" (process-cover-title options)})

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn process-cover!
  ; @ignore
  ;
  ; @param (map) options
  [options]
  (reset! process.state/COVER (process-cover options)))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn process-common-subtitle
  ; @ignore
  ;
  ; @param (map) options
  ; {:lib-name (string)
  ;  :website (string)(opt)}
  ;
  ; @example
  ; (process-common-subtitle {...})
  ; =>
  ; "Functional documentation of the [my-repository](...) Clojure / ClojureScript library"
  ;
  ; @return (string)
  [{:keys [lib-name website]}]
  (let [clj-library?  (process.env/clj-library?)
        cljs-library? (process.env/cljs-library?)]
       (str "Functional documentation of the "
            (if website (str "["lib-name"]("website")")
                        (str "<strong>"lib-name"</strong>"))
            (if clj-library?  " Clojure ")
            (if (and clj-library? cljs-library?) "/")
            (if cljs-library? " ClojureScript ")
            "library")))

(defn process-common-credits
  ; @ignore
  ;
  ; @param (map) options
  ;
  ; @example
  ; (process-common-credits {...})
  ; =>
  ; "..."
  ;
  ; @return (string)
  [_]
  (str "<sub>This documentation is generated with the [clj-docs-generator](https://github.com/monotech-tools/clj-docs-generator) engine.</sub>\n"))

(defn process-common
  ; @ignore
  ;
  ; @param (map) options
  ;
  ; @return (map)
  [options]
  {"credits"  (process-common-credits  options)
   "subtitle" (process-common-subtitle options)})

(defn process-common!
  ; @ignore
  ;
  ; @param (map) options
  [options]
  (reset! process.state/COMMON (process-common options)))
