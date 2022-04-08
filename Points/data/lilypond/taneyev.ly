\version "2.18.0"

voiceA = \relative c'' {
	\voiceOne
	\set subdivideBeams = ##t
  	\set baseMoment = #(ly:make-moment 1/8)
  	\set beatStructure = #'(2 2 2 2)
	\omit TupletNumber
	\omit TupletBracket
	%Bar 1
	c8 s8 s4

	%Bar 2
	S2
	%Bar 3
	\tuplet 3/2 {gis16[ ais bis} \tuplet 3/2 {cis dis e]} \tuplet 3/2 {dis[ cis b} \tuplet 3/2 {ais gis fisis]}

	%Bar 4
	\tuplet 3/2 {gis8[ dis'16]} \tuplet 3/2 {gis8.~} \tuplet 3/2 {gis16 fisis fis} \tuplet 3/2 {eis e dis}
}

voiceB = \relative c'' {
	\voiceTwo
	\set subdivideBeams = ##t
  	\set baseMoment = #(ly:make-moment 1/8)
  	\set beatStructure = #'(2 2 2 2)

	%Bar 1
	\tuplet 3/2 {c16[ d ees} \tuplet 3/2 {f g aes]} 
	\omit TupletNumber
	\omit TupletBracket
	\tuplet 3/2 {g[ f ees} \tuplet 3/2 {d c b]}

	%Bar 2
	\tuplet 3/2 {bes16[ c des} \tuplet 3/2 {ees f ges]} \tuplet 3/2 {f[ ees des} \tuplet 3/2 {c bes a]}

	%Bar 3
	gis8 r fisis r

	%Bar 4
	\tuplet 3/2 {gis16 ais b} \tuplet 3/2 {cis dis e} \tuplet 3/2 {dis8[ cisis16]} \tuplet 3/2 {cis bis b}

	%Bar 5
	\tuplet 3/2 {<fis d'> <cis' e> <fis, d'>}
	\tuplet 3/2 {<cis' fis> <fis, d'> <cis' e>}
	\tuplet 3/2 {<fis, cis'> <ais d> <fis cis'>}
	\tuplet 3/2 {<ais fis'> <fis cis'> <ais d>}
}

voiceC = \relative c {
	\voiceOne

	%Bar 1
	<c, ees g c>16 s4..

	%Bar 2
	f'2

	%Bar 3
	<e, gis cis e>16 <dis dis'> <e e'> <cis cis'> <dis ais' dis> <cisis cisis'> <dis dis'> < dis dis,>

	%Bar 4
	r8 <ais' ais'> <b b'> <fis, fis'>

	%Bar 5
	<ais' fis' ais> <gisis fis' gisis> <ais fis' ais> <fis, fis'>
	
}


voiceD = \relative c {
	\voiceTwo

	%Bar 1
	s16 b'\noBeam c g b ais b g

	%Bar 2
	des c des bes a gis a f

	%Bar 3
	
}

\score {
  \new PianoStaff <<
    \new Staff {
		\clef treble
		\key gis \minor
		\time 2/4
	    \set Score.currentBarNumber = #95
        \bar ""
		<<
			\new Voice = "voiceA" \voiceA
			\new Voice = "voiceB" \voiceB
		>>
	}
	\new Staff {
		\clef bass
		\key gis \minor
		\time 2/4
		<<
			\new Voice = "voiceC" \voiceC
			\new Voice = "voiceD" \voiceD
		>>
	}
  >>
  \layout {
	indent = #0
  }
  \midi { 
	\tempo 4=100
}
}