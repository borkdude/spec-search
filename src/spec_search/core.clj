(ns spec-search.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]))

(defn search*
  [[sym spec] args ret opts]
  (let [args-spec (:args spec)
        ret-spec (:ret spec)
        exact-ret-match? (:exact-ret-match? opts)]
    (let [args-match? (or (not args)
                          (and args args-spec
                               (s/valid? args-spec
                                         (second args))))
          ret-match? (or (not ret)
                         (and ret ret-spec
                              (s/valid? ret-spec (second ret))))
          ret-match? (if (and ret ret-match? exact-ret-match?)
                       (= (second ret)
                          (try (apply (resolve sym) (second args))
                               (catch Exception _ false)))
                       ret-match?)]
      (when (and args-match?
                 ret-match?) sym))))

(defn search
  "Search spec that matches args and/or ret opt.
   Argument opts can have:
    :args - value to search matching args spec
    :ret  - value to search matching ret spec
    :exact-ret-match? - if true, the call with args must be equal to ret

   At minimum args or ret must be specified. When one is missing,
   corresponding spec always matches."
  [opts]
  (let [args (find opts :args)
        ret (find opts :ret)
        _ (assert (or args ret) "At minimum provide args or ret")
        syms (stest/instrumentable-syms)
        specs (map s/get-spec (stest/instrumentable-syms))
        sym*spec (zipmap syms specs)]
    (keep #(search* % args ret opts) sym*spec)))


;;;; Scratch

(comment
  
  (require '[speculative.core])
  (search {:args [inc [1 2 3]] :ret [2 3 4]})
  (search {:args [inc [1 2 3]] :ret [2 3 4] :exact-ret-match? true})

  )