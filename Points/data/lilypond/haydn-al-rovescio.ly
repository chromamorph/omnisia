\version "2.18.0"

upper = \relative c' {
  \clef treble
  \key a \major
  \time 3/4

	\repeat volta 2 {
	<cis a'>2.
	<d gis>4 e b'
	<< {a b cis} \\ {e,2.}>>
	<< {d'2.} \\ {b4 a gis}>>
	<< {cis4 d e} \\ {a,2.}>>
	<< {fis'2.} \\ {a,2.}>>
	<< {e'4 gis a} \\ {a,2.}>>
	b'4 <b, d> <a cis>
	b'4 <b, d> <a cis>
	 <gis b>2. }

	\break

	\repeat volta 2 {
	<gis b>2.
	<a cis>4 <b d> b'
	<a, cis>4 <b d> b'
	<<{a gis e}\\{a,2.}>>
	<a fis'>2.
	<<{e'4 d cis}\\{a2.}>>
	<<{d2.}\\{gis,4 a b}>>
	<<{cis b a}\\{e2.}>>
	b'4 e, <d gis>4
	<cis a'>2.
	}
}

lower = \relative c {
  \clef bass
  \key a \major
  \time 3/4

	a4 e' a
	e e' d
	cis b a 
	gis fis e
	a b cis
	d d, d'
	cis e fis
	<gis, e'>2 <a e'>4
	<gis e'>2 <a e'>4
	e4 e' e,

	e4 e' e,
	<a e'>4 <gis e'>2
	<a e'>4 <gis e'>2
	fis'4 e cis
	d d, d'
 	cis b a
	e fis gis
	a b cis
	d e e,
	a e a,
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
	\tempo 4 = 120
}
}