(ns haiku-chain.core
  (:require digest)
  (:import [java.util Base64]))

(def WORKLOAD
  1)

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

(defn work-bits
  [block]
  (take
   WORKLOAD
   (.decode (Base64/getDecoder)
            (.getBytes (block-hash block)))))

(defn valid-block?
  [block]
  (every? (partial = 0)
          (work-bits block)))

(defn find-nonce
  [block]
  (first
   (filter valid-block?
           (for [nonce (range)] (assoc block :nonce nonce)))))

(comment
  (valid-block? initial-block)
  (find-nonce initial-block)
  (take 10 (for [nonce (range)] (assoc initial-block :nonce nonce)))
  (map work-bits (take 10 (for [nonce (range)] (assoc initial-block :nonce nonce))))
  )
