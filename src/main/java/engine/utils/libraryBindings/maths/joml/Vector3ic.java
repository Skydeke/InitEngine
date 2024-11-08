/*
 * (C) Copyright 2016-2018 JOML

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package engine.utils.libraryBindings.maths.joml;

import engine.utils.libraryBindings.opengl.shaders.UniformValue;
import org.lwjgl.opengl.GL20;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * Interface to a read-only view of a 3-dimensional vector of integers.
 *
 * @author Kai Burjack
 */
public interface Vector3ic extends UniformValue {

    @Override
    default void load(int location) {
        GL20.glUniform3i(location, x(), y(), z());
    }

    /**
     * @return the value of the x component
     */
    int x();

    /**
     * @return the value of the y component
     */
    int y();

    /**
     * @return the value of the z component
     */
    int z();

    /**
     * Store this vector into the supplied {@link IntBuffer} at the current
     * buffer {@link IntBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given IntBuffer.
     * <p>
     * In order to specify the offset into the IntBuffer at which the vector is
     * stored, use {@link #get(int, IntBuffer)}, taking the absolute position as
     * parameter.
     *
     * @param buffer will receive the values of this vector in <code>x, y, z</code> order
     * @return the passed in buffer
     * @see #get(int, IntBuffer)
     */
    IntBuffer get(IntBuffer buffer);

    /**
     * Store this vector into the supplied {@link IntBuffer} starting at the
     * specified absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given IntBuffer.
     *
     * @param index  the absolute position into the IntBuffer
     * @param buffer will receive the values of this vector in <code>x, y, z</code> order
     * @return the passed in buffer
     */
    IntBuffer get(int index, IntBuffer buffer);

    /**
     * Store this vector into the supplied {@link ByteBuffer} at the current
     * buffer {@link ByteBuffer#position() position}.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     * <p>
     * In order to specify the offset into the ByteBuffer at which the vector is
     * stored, use {@link #get(int, ByteBuffer)}, taking the absolute position
     * as parameter.
     *
     * @param buffer will receive the values of this vector in <code>x, y, z</code> order
     * @return the passed in buffer
     * @see #get(int, ByteBuffer)
     */
    ByteBuffer get(ByteBuffer buffer);

    /**
     * Store this vector into the supplied {@link ByteBuffer} starting at the
     * specified absolute buffer position/index.
     * <p>
     * This method will not increment the position of the given ByteBuffer.
     *
     * @param index  the absolute position into the ByteBuffer
     * @param buffer will receive the values of this vector in <code>x, y, z</code> order
     * @return the passed in buffer
     */
    ByteBuffer get(int index, ByteBuffer buffer);

    /**
     * Store this vector at the given off-heap memory address.
     * <p>
     * This method will throw an {@link UnsupportedOperationException} when JOML is used with `-Djoml.nounsafe`.
     * <p>
     * <em>This method is unsafe as it can result in a crash of the JVM process when the specified address range does not belong to this process.</em>
     *
     * @param address the off-heap address where to store this vector
     * @return this
     */
    Vector3ic getToAddress(long address);

    /**
     * Subtract the supplied vector from this one and store the result in
     * <code>dest</code>.
     *
     * @param v    the vector to subtract
     * @param dest will hold the result
     * @return dest
     */
    Vector3i sub(Vector3ic v, Vector3i dest);

    /**
     * Decrement the components of this vector by the given values and store the
     * result in <code>dest</code>.
     *
     * @param x    the x component to subtract
     * @param y    the y component to subtract
     * @param z    the z component to subtract
     * @param dest will hold the result
     * @return dest
     */
    Vector3i sub(int x, int y, int z, Vector3i dest);

    /**
     * Add the supplied vector to this one and store the result in
     * <code>dest</code>.
     *
     * @param v    the vector to add
     * @param dest will hold the result
     * @return dest
     */
    Vector3i add(Vector3ic v, Vector3i dest);

    /**
     * Increment the components of this vector by the given values and store the
     * result in <code>dest</code>.
     *
     * @param x    the x component to add
     * @param y    the y component to add
     * @param z    the z component to add
     * @param dest will hold the result
     * @return dest
     */
    Vector3i add(int x, int y, int z, Vector3i dest);

    /**
     * Multiply the components of this vector by the given scalar and store the result in <code>dest</code>.
     *
     * @param scalar the value to multiply this vector's components by
     * @param dest   will hold the result
     * @return dest
     */
    Vector3i mul(int scalar, Vector3i dest);

    /**
     * Multiply the supplied vector by this one and store the result in
     * <code>dest</code>.
     *
     * @param v    the vector to multiply
     * @param dest will hold the result
     * @return dest
     */
    Vector3i mul(Vector3ic v, Vector3i dest);

    /**
     * Multiply the components of this vector by the given values and store the
     * result in <code>dest</code>.
     *
     * @param x    the x component to multiply
     * @param y    the y component to multiply
     * @param z    the z component to multiply
     * @param dest will hold the result
     * @return dest
     */
    Vector3i mul(int x, int y, int z, Vector3i dest);

    /**
     * Return the length squared of this vector.
     *
     * @return the length squared
     */
    long lengthSquared();

    /**
     * Return the length of this vector.
     *
     * @return the length
     */
    double length();

    /**
     * Return the distance between this Vector and <code>v</code>.
     *
     * @param v the other vector
     * @return the distance
     */
    double distance(Vector3ic v);

    /**
     * Return the distance between <code>this</code> vector and <code>(x, y, z)</code>.
     *
     * @param x the x component of the other vector
     * @param y the y component of the other vector
     * @param z the z component of the other vector
     * @return the euclidean distance
     */
    double distance(int x, int y, int z);

    /**
     * Return the square of the distance between this vector and <code>v</code>.
     *
     * @param v the other vector
     * @return the squared of the distance
     */
    long distanceSquared(Vector3ic v);

    /**
     * Return the square of the distance between <code>this</code> vector and <code>(x, y, z)</code>.
     *
     * @param x the x component of the other vector
     * @param y the y component of the other vector
     * @param z the z component of the other vector
     * @return the square of the distance
     */
    long distanceSquared(int x, int y, int z);

    /**
     * Negate this vector and store the result in <code>dest</code>.
     *
     * @param dest will hold the result
     * @return dest
     */
    Vector3i negate(Vector3i dest);

    /**
     * Set the components of <code>dest</code> to be the component-wise minimum of this and the other vector.
     *
     * @param v    the other vector
     * @param dest will hold the result
     * @return dest
     */
    Vector3i min(Vector3ic v, Vector3i dest);

    /**
     * Set the components of <code>dest</code> to be the component-wise maximum of this and the other vector.
     *
     * @param v    the other vector
     * @param dest will hold the result
     * @return dest
     */
    Vector3i max(Vector3ic v, Vector3i dest);

    /**
     * Get the value of the specified component of this vector.
     *
     * @param component the component, within <code>[0..2]</code>
     * @return the value
     * @throws IllegalArgumentException if <code>component</code> is not within <code>[0..2]</code>
     */
    int get(int component) throws IllegalArgumentException;

}
