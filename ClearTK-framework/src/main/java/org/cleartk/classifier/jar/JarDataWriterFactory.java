/** 
 * Copyright (c) 2009, Regents of the University of Colorado 
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
package org.cleartk.classifier.jar;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.cleartk.classifier.DataWriterFactory;
import org.cleartk.classifier.encoder.features.FeaturesEncoder;
import org.cleartk.classifier.encoder.features.FeaturesEncoder_ImplBase;
import org.cleartk.classifier.encoder.outcome.OutcomeEncoder;
import org.cleartk.util.ReflectionUtil;
import org.uimafit.descriptor.ConfigurationParameter;
import org.uimafit.factory.ConfigurationParameterFactory;
import org.uimafit.util.InitializeUtil;
import org.uimafit.util.initialize.Initializable;

public abstract class JarDataWriterFactory<FEATURES_OUT_TYPE, OUTCOME_IN_TYPE, OUTCOME_OUT_TYPE> implements
		DataWriterFactory<OUTCOME_IN_TYPE>, Initializable {

	public static final String PARAM_OUTPUT_DIRECTORY = 
			ConfigurationParameterFactory.createConfigurationParameterName(
					JarDataWriterFactory.class, 
					"outputDirectory");
	@ConfigurationParameter(
			mandatory = false, 
			description = "provides the name of the directory where the " +
					"training data will be written.  if you do not set this " +
					"parameter, then you must call setOutputDirectory directly.")
	protected File outputDirectory;
	

	public static final String PARAM_LOAD_ENCODERS_FROM_FILE_SYSTEM = 
			ConfigurationParameterFactory.createConfigurationParameterName(
					JarDataWriterFactory.class, 
					"loadEncodersFromFileSystem");
	@ConfigurationParameter(
			mandatory = false,
			description = "when true indicates that the FeaturesEncoder and " +
					"OutcomeEncoder should be loaded from the file system " +
					"instead of being created by the DataWriterFactory", 
			defaultValue = "false")
	private boolean loadEncodersFromFileSystem = false;

	
	public void initialize(UimaContext context) throws ResourceInitializationException {
		InitializeUtil.initialize(this, context);
		if (loadEncodersFromFileSystem) {
			try {
				File encoderFile = new File(outputDirectory, FeaturesEncoder_ImplBase.ENCODERS_FILE_NAME);

				if (!encoderFile.exists()) {
					throw new RuntimeException(String.format("No encoder found in directory %s", outputDirectory));
				}

				ObjectInputStream is = new ObjectInputStream(new FileInputStream(encoderFile));

				// read the FeaturesEncoder and check the types
				FeaturesEncoder<?> untypedFeaturesEncoder = FeaturesEncoder.class.cast(is.readObject());
				ReflectionUtil.checkTypeParameterIsAssignable(FeaturesEncoder.class, "FEATURES_OUT_TYPE",
						untypedFeaturesEncoder, JarDataWriterFactory.class, "FEATURES_OUT_TYPE", this);

				// read the OutcomeEncoder and check the types
				OutcomeEncoder<?, ?> untypedOutcomeEncoder = OutcomeEncoder.class.cast(is.readObject());
				ReflectionUtil.checkTypeParameterIsAssignable(OutcomeEncoder.class, "OUTCOME_IN_TYPE", untypedOutcomeEncoder,
						JarDataWriterFactory.class, "OUTCOME_IN_TYPE", this);
				ReflectionUtil.checkTypeParameterIsAssignable(OutcomeEncoder.class, "OUTCOME_OUT_TYPE",
						untypedOutcomeEncoder, JarDataWriterFactory.class, "OUTCOME_OUT_TYPE", this);

				// assign the encoders to the instance variables
				this.featuresEncoder = ReflectionUtil.uncheckedCast(untypedFeaturesEncoder);
				this.outcomeEncoder = ReflectionUtil.uncheckedCast(untypedOutcomeEncoder);
				is.close();
			}
			catch (Exception e) {
				throw new ResourceInitializationException(e);
			}
		}
	}

	protected boolean setEncodersFromFileSystem(
			JarDataWriter<OUTCOME_IN_TYPE, OUTCOME_OUT_TYPE, FEATURES_OUT_TYPE> dataWriter) {
		if (this.featuresEncoder != null && this.outcomeEncoder != null) {
			dataWriter.setFeaturesEncoder(this.featuresEncoder);
			dataWriter.setOutcomeEncoder(this.outcomeEncoder);
			return true;
		}
		return false;
	}

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}
	
	protected FeaturesEncoder<FEATURES_OUT_TYPE> featuresEncoder = null;
	protected OutcomeEncoder<OUTCOME_IN_TYPE, OUTCOME_OUT_TYPE> outcomeEncoder = null;

}
