/**
 * This package contains the in-memory representations for the fundamental LISP atom and list
 * structures.
 * <p>
 * The {@link org.ulithi.jlisp.mem.Cell} is the fundamental unit of storage. A cell contains exactly
 * two s-expressions. An s-expression can be either an atom, or a list (conversely, atoms and lists
 * are both types of s-expressions. Finally, an atom can be a literal integer or character sequence,
 * or it can be a symbol.
 * <p>
 * After initial lexical analysis by {@link org.ulithi.jlisp.lexer.Lexer}, the parser generates
 * {@link PTree} (Parse Tree) objects. A {@code PTree} is a set of linked {@code Cells} that are the
 * in-memory representation of a LISP statement.
 * <p>
 * Note that dotted pairs are primarily string representations of {@code Cell Cells}, and may be
 * used to encode data passed into and out of the interpreter. There is no direct in-memory structure
 * called a dotted pair however.
 * <p>
 * References include:<ul>
 *     <li>https://iamwilhelm.github.io/bnf-examples/lisp (LISP BNF)</li>
 *     <li>http://lisp2d.net/teach/i.php (internal form of lists)</li>
 *     <li>http://watson.latech.edu/book/objects/objectsFunctional.html (functional programming)</li>
 * </ul>
 */
package org.ulithi.jlisp.mem;
