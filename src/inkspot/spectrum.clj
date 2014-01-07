(ns inkspot.spectrum)

(defn wavelength-color
  ([wavelength] (wavelength-color wavelength 1))
  ([wavelength gamma]
    (let [seg1 (fn [a b] (double (/ (- b wavelength) (- b a))))
          seg2 (fn [a b] (double (/ (- wavelength a) (- b a))))
          intensity1 (fn [a b] (* gamma (+ 0.3 (* 0.7 (seg1 a b)))))
          intensity2 (fn [a b] (* gamma (+ 0.3 (* 0.7 (seg2 a b)))))
          bands [[  380 { :r 0 :g 0 :b 0 :s 0 } ]
                 [  420 { :r (seg1 380 440) :g 0 :b 1 :s (intensity2 380 420) } ]
                 [  440 { :r (seg1 380 440) :g 0 :b 1 :s gamma } ]
                 [  490 { :r 0 :g (seg2 440 490) :b 1 :s gamma } ]
                 [  510 { :r 0 :g 1 :b (seg1 490 510) :s gamma } ]
                 [  580 { :r (seg2 510 580) :g 1 :b 0 :s gamma } ]
                 [  645 { :r 1 :g (seg1 580 645) :b 0 :s gamma } ]
                 [  700 { :r 1 :g 0 :b 0 :s gamma } ]
                 [  780 { :r 1 :g 0 :b 0 :s (intensity1 700 780) } ]
                 [ 9999 { :r 0 :g 0 :b 0 :s 0 } ]]]
        (second (first (drop-while #(< (first %) wavelength) bands))))))

(defn frequency-color
  ([freq] (frequency-color freq 1))
  ([freq gamma] (wavelength-color (/ 299724.58 freq) gamma)))

(defn rgb [{red :r green :g blue :b scale :s :or {scale 1}}]
  (+ (bit-shift-left (int (* 255 red scale)) 16)
     (bit-shift-left (int (* 255 green scale)) 8)
     (int (* 255 blue scale))))