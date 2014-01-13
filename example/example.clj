(ns inkspot.examples
  (require [clojure.java.io :as io]
           [inkspot.color :as color]
           [inkspot.color-chart :as cc]
           [inkspot.color-chart.lindsay :as lindsay])
  (import [javax.imageio ImageIO]))

(let [palettes {
        :web-safe-colors (map color/coerce cc/web-safe-colors)
        :spectrum        (cc/spectrum 216)
        :rainbow         (cc/rainbow 216)
        :lindsay         (map color/coerce (vals lindsay/swatch))
        :gradient1       (cc/gradient :orange :blue 216)
        :gradient2       (cc/gradient :red :snow 216)}]
  (doseq [[k v] palettes
        :let [f (io/file (str "example/palette/" (name k) ".png"))]]
    (ImageIO/write (cc/create-palette v) "png" f)))