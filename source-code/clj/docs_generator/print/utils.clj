
(ns docs-generator.print.utils
    (:require [fruits.vector.api :as vector]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn sort-functions
  ; @ignore
  ;
  ; @param (maps in vector) functions
  ;
  ; @return (maps in vector)
  [functions]
  (vector/sort-items-by functions #(get % "name")))

(defn sort-constants
  ; @ignore
  ;
  ; @param (maps in vector) constants
  ;
  ; @return (maps in vector)
  [constants]
  (vector/sort-items-by constants #(get % "name")))
