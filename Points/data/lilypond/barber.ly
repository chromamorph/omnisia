\version "2.18.0"

upper = \relative c'' {
  \set Score.currentBarNumber = #25
  \bar ""
  \clef treble
  \key c \major
  \time 4/4

  \tuplet 3/2 {b' c g'}
  \tuplet 3/2 {e cis a}
  \tuplet 3/2 {f ges d'}
  \tuplet 3/2 {bes g ees}
  \tuplet 3/2 {b8[ c g'}
  \tuplet 3/2 {e cis a]}
  \tuplet 3/2 {f[ ges d'}
  \tuplet 3/2 {bes g ees]}
}

lower = \relative c' {

  \clef treble
  \key c \major
  \time 4/4

  \tuplet 3/2 {b[ ees g}
  \tuplet 3/2 {bes d ges,]}
  \tuplet 3/2 {f[ a cis}
  \tuplet 3/2 {e g c,]}

  \tuplet 3/2 {b,[ ees g}
  \tuplet 3/2 {bes d ges,]}
  \tuplet 3/2 {f[ a cis}
  \tuplet 3/2 {e g c,]}

  r4 \clef bass g,, aes8. des16 aes4
}

\score {
  \new PianoStaff <<
    \new Staff = "upper" \upper
    \new Staff = "lower" \lower
  >>
  \layout { 
	indent = #0
  }
  \midi { 
	\tempo 4=100
  }
}