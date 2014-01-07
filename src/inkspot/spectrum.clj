(ns inkspot.spectrum)

(defn wavelength-color
  ([wavelength] (wavelength-color wavelength 1))
  ([wavelength gamma]
    (let [seg1 (fn [a b] (double (/ (- b wavelength) (- b a))))
          seg2 (fn [a b] (double (/ (- wavelength a) (- b a))))
          intensity1 (fn [a b] (* gamma (+ 0.3 (* 0.7 (seg1 a b)))))
          intensity2 (fn [a b] (* gamma (+ 0.3 (* 0.7 (seg2 a b)))))
          bands [[  380 { :red 0 :green 0 :blue 0 :scale 0 } ]
                 [  420 { :red (seg1 380 440) :green 0 :blue 1 :scale (intensity2 380 420) } ]
                 [  440 { :red (seg1 380 440) :green 0 :blue 1 :scale gamma } ]
                 [  490 { :red 0 :green (seg2 440 490) :blue 1 :scale gamma } ]
                 [  510 { :red 0 :green 1 :blue (seg1 490 510) :scale gamma } ]
                 [  580 { :red (seg2 510 580) :green 1 :blue 0 :scale gamma } ]
                 [  645 { :red 1 :green (seg1 580 645) :blue 0 :scale gamma } ]
                 [  700 { :red 1 :green 0 :blue 0 :scale gamma } ]
                 [  780 { :red 1 :green 0 :blue 0 :scale (intensity1 700 780) } ]
                 [ 9999 { :red 0 :green 0 :blue 0 :scale 0 } ]]]
        (second (first (drop-while #(< (first %) wavelength) bands))))))

(defn frequency-color
  ([freq] (frequency-color freq 1))
  ([freq gamma] (wavelength-color (/ 299724.58 freq) gamma)))

