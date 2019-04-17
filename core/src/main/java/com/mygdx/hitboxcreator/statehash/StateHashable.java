package com.mygdx.hitboxcreator.statehash;




/** Interface for the the dedicated state hash logic.
 * {@link #computeStateHash()} method is required to not clash with the original {@link Object#hashCode()}.
 *
 * @author crashinvaders
 * */
public interface StateHashable {
    int computeStateHash();
}
