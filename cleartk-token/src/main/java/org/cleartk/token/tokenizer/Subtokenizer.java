 /** 
 * Copyright (c) 2007-2008, Regents of the University of Colorado 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * Neither the name of the University of Colorado at Boulder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. 
*/
package org.cleartk.token.tokenizer;

import java.util.regex.Pattern;

/**
 * <br>Copyright (c) 2007-2008, Regents of the University of Colorado 
 * <br>All rights reserved.

 * <p>
 * 
 * This tokenizer is named 'Subtokenizer' because it is designed to
 * over-generate tokens that can then be used as input for another tokenization
 * approach. Specifically, this tokenizer can be used as input for BIO-styled
 * tokenization using a classifier. Each token generated by this tokenizer would
 * be assigned a B-TOKEN (or something similar) or I-TOKEN for a given set of
 * gold-standard tokens or based on the results of a classifier.
 * <p>
 * 
 * Please see the corresponding unit tests for examples of how this tokenizer
 * produces tokens.
 * 
 * @author Philip
 * 
 */
public class Subtokenizer extends Tokenizer_ImplBase {

	public static String subtokensRegex = "([a-zA-Z]+|[0-9]+|\\W)";

	public static Pattern subtokensPattern = Pattern.compile(subtokensRegex, Pattern.MULTILINE);

	public static String multipleWhitespaceRegex = "(\\s+)";

	public static Pattern multipleWhitespacePattern = Pattern.compile(multipleWhitespaceRegex, Pattern.MULTILINE);

	public String[] getTokenTexts(String text) {
		text = subtokensPattern.matcher(text).replaceAll(" $1 ");
		text = multipleWhitespacePattern.matcher(text).replaceAll(" ");
		text = text.trim();
		return text.split(" ");
	}

}
