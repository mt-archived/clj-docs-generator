
(ns docs-generator.detect.env
    (:require [docs-generator.detect.state :as detect.state]
              [fruits.string.api           :as string]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn code-dir
  ; @ignore
  ;
  ; @param (map) options
  ; @param (string) layer-name
  ; @param (string) api-filepath
  ; @param (string) alias
  ;
  ; @usage
  ; (code-dir {...} "clj" "submodules/my-repository/source/code/clj/my_directory/api.clj" "api")
  ; =>
  ; "source-code/clj"
  ;
  ; @return (string)
  [{:keys [code-dirs]} layer-name api-filepath _]
  ; Finds out which code-dir belongs to the taken api-filepath file.
  (letfn [(f0 [[code-dir %]] (if (= % api-filepath)
                                 (-> code-dir)))]
         (let [api-files (get-in @detect.state/LAYERS [layer-name])]
              (some f0 api-files))))
