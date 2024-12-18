

;(in-package :common-lisp-user)
(load
 (merge-pathnames
  (make-pathname
   :directory '(:relative "File conversion")
   :name "csv-files"
   :type "lisp")
  *lisp-code-root*))
(load
 (merge-pathnames
  (make-pathname
   :directory '(:relative "File conversion")
   :name "humdrum-by-col"
   :type "lisp")
  *lisp-code-root*))
(load
 (merge-pathnames
  (make-pathname
   :directory '(:relative "File conversion")
   :name "midi-save"
   :type "lisp")
  *lisp-code-root*))
(load
 (merge-pathnames
  (make-pathname
   :directory '(:relative "Pattern rating")
   :name "musical-properties"
   :type "lisp")
  *lisp-code-root*))
(load
 (merge-pathnames
  (make-pathname
   :directory '(:relative "Pattern discovery")
   :name "structural-induction-mod"
   :type "lisp")
  *lisp-code-root*))

(setq
 *path&name*
 (merge-pathnames
  (make-pathname
   :directory
   '(:relative
     "gibbonsSilverSwan1612" "monophonic" "repeatedPatterns"
     "tomCollins" "F"))
  *music-data-root*))

(setq
 csv-destination
 (merge-pathnames
  (make-pathname
   :directory
   '(:relative "csv") :name "silverswan" :type "csv")
  *path&name*))
(setq
 MIDI-destination
 (merge-pathnames
  (make-pathname
   :directory
   '(:relative "midi") :name "silverswan" :type "mid")
  *path&name*))
(setq
 dataset-destination
 (merge-pathnames
  (make-pathname
   :directory
   '(:relative "lisp") :name "silverswan" :type "txt")
  *path&name*))
(progn
  (setq *scale* 1000)
  (setq *anacrusis* 0)
  (setq
   dataset
   (dataset-restricted-to-m-in-nth
    (humdrum-file2dataset-by-col
     (merge-pathnames
      (make-pathname
       :directory
       '(:relative "kern") :name "silverswan" :type "krn")
      *path&name*)) 2 4))
  (saveit
   MIDI-destination
   (modify-to-check-dataset dataset *scale*))
  (write-to-file
   (mapcar
    #'(lambda (x)
        (append
         (list (+ (first x) *anacrusis*))
         (rest x)))
    dataset)
   dataset-destination)
  (dataset2csv
   dataset-destination csv-destination))





