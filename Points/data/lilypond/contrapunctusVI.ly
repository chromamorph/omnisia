\version "2.18.0"

\header {
	tagline = ""
}

soprano = \relative c'' {
  \clef treble
  \key d \minor
  \time 4/4
R1 a'4 d,8. e16 f8. g16 a4 bes a8. g16 f4~ f16 e f g 
a8. g16 f e d c bes4 a~ a8. b16 c4~ c8. b16 cis8. d16
}

alto = \relative c'' {
  \clef treble
  \key d \minor
  \time 4/4
R1 R1 r2 d,4 a'8. g16 f8. e16 d4 cis d8. e16 f4~ f16 g f e d4 e8. f16 
}



bass = \relative c {
  \clef bass
  \key d \minor
  \time 4/4
d2 a'4. g8 f4. e8 d2 cis d4. e8 f2~ f8. g16 f8. e16 d8. d,16 e8. 
f16 g8. a16 g8. f16
}

\score {
  <<
    \new Staff \with {instrumentName = #"Soprano" shortInstrumentName = #"S."} \soprano
    \new Staff \with {instrumentName = #"Alto" shortInstrumentName = #"A."} \alto
    \new Staff \with {instrumentName = #"Bass" shortInstrumentName = #"B."} \bass
  >>
  \layout {
}
  \midi { 
	\tempo 4 = 100
}
}

