(ns inkspace.color-test
  (:use-macros [cljs-test.macros :only [deftest is= is]])
  (:require [cljs-test.core :as test]
            [inkspot.color :refer [coerce rgba]]))

(deftest check-coerce
  (is= (coerce :red) [255,0,0,1]))

(deftest check-rgba
  (is= (rgba (coerce [12.23 45.54 65.43])) "rgba(12,45,65,1)"))
