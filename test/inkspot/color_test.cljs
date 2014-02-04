(ns inkspace.color-test
  (:use-macros [cljs-test.macros :only [deftest is= is]])
  (:require [cljs-test.core :as test]
            [inkspot.color :refer [coerce]]))

(deftest check-coerce
  (is= (coerce :red) "rgba(255,0,0,1)"))
