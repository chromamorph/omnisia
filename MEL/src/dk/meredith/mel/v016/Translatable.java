package dk.meredith.mel.v016;

import java.util.Collection;

public interface Translatable<T> {
	T translate(VectorSum vectorSum);
	Collection<T> translate(Collection<VectorSum> vectorSumCollection);
}
