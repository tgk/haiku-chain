(ns haiku-chain.core
  (:require digest)
  (:import [java.util Base64]))

(def WORKLOAD
  2)

(def initial-block
  {:prev-hash "b4d455"
   :items []})

(defn create-block
  [prev-hash & items]
  {:prev-hash prev-hash
   :items items})

(defn block-hash
  [{:keys [prev-hash nonce items] :as block}]
  (digest/sha-256
   (str prev-hash nonce (apply str items))))

(defn byte->bits
  [byte]
  (for [bit (range 8)]
    (not= 0 (bit-and byte (bit-shift-left 1 bit)))))

(defn byte-array->bits
  [ba]
  (apply concat (map byte->bits ba)))

(defn work-bits
  [block]
  (take
   WORKLOAD
   (byte-array->bits
    (.decode (Base64/getDecoder)
             (block-hash block)))))

(defn valid-block?
  [block]
  (every? (partial = false)
          (work-bits block)))

(defn find-nonce
  [block]
  (first
   (filter valid-block?
           (for [nonce (range)] (assoc block :nonce nonce)))))

(comment
  (work-bits initial-block)
  (valid-block? initial-block)
  (def foo
    (find-nonce initial-block))
  (take 10 (for [nonce (range)] (assoc initial-block :nonce nonce)))
  (map work-bits (take 100 (for [nonce (range)] (assoc initial-block :nonce nonce))))
  (map block-hash
       (take 10 (for [nonce (range)] (assoc initial-block :nonce nonce))))
  )
