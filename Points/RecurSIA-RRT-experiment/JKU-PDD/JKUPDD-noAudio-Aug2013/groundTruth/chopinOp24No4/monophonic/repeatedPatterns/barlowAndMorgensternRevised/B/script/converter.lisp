

;(in-package :common-lisp-user)
(load
 (merge-pathnames
  (make-pathname
   :directory '(:relative "File conversion")
   :name "csv-files"
   :type "lisp")
  *MCStylistic-Mar2013-functions-path*))
(load
 (merge-pathnames
  (make-pathname
   :directory '(:relative "File conversion")
   :name "kern-by-col"
   :type "lisp")
  *MCStylistic-Mar2013-functions-path*))
(load
 (merge-pathnames
  (make-pathname
   :directory '(:relative "File conversion")
   :name "midi-save"
   :type "lisp")
  *MCStylistic-Mar2013-functions-path*))

(setq
 *music-data-root*
 (make-pathname
  :directory
  '(:absolute
    "Users" "tomthecollins" "Shizz" "Data" "Music")))

(setq
 *path&name*
 (merge-pathnames
  (make-pathname
   :directory
   '(:relative
     "chopinOp24No4" "monophonic"
     "repeatedPatterns" "barlowAndMorgensternRevised" "B"))
  *music-data-root*))

(setq
 csv-destination
 (merge-pathnames
  (make-pathname
   :directory
   '(:relative "csv") :name "mazurka24-4" :type "csv")
  *path&name*))
(setq
 MIDI-destination
 (merge-pathnames
  (make-pathname
   :directory
   '(:relative "midi") :name "mazurka24-4" :type "mid")
  *path&name*))
(setq
 dataset-destination
 (merge-pathnames
  (make-pathname
   :directory
   '(:relative "lisp") :name "mazurka24-4" :type "txt")
  *path&name*))
(progn
  (setq *scale* 1000)
  (setq *anacrusis* 0)
  (setq
   dataset
   (sky-line-clipped
    (kern-file2dataset-by-col
     (merge-pathnames
     (make-pathname
      :directory
      '(:relative "kern") :name "mazurka24-4"
      :type "krn")
     *path&name*))))
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





