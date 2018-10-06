(ns inkspot.color-chart-test
  (:use-macros
   [cljs-test.macros :only [deftest is= is]])
  (:require
   [cljs-test.core :as test]
   [inkspot.color :refer [coerce rgba]]
   [inkspot.color-chart :refer [ui-gradient]]))

(deftest check-ui-gradient
  (let [[a b] (ui-gradient :moss 2)]
    (is= (coerce a) "rgba(19,78,94,1)")
    (is= (coerce b) "rgba(66,128,111,1)")))
