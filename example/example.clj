(ns inkspot.examples
  (require [clojure.java.io :as io]
           [inkspot.color-chart :as cc]
           [inkspot.color-chart.lindsay :as lindsay])
  (import [javax.imageio ImageIO]))

(let [palettes {
        :web-safe-colors cc/web-safe-colors
        :spectrum        (cc/spectrum 216)
        :rainbow         (cc/rainbow 216)
        :lindsay         (vals lindsay/swatch)}]
  (doseq [[k v] palettes
        :let [f (io/file (str "example/palette/" (name k) ".png"))]]
    (ImageIO/write (cc/create-palette v) "png" f)))
