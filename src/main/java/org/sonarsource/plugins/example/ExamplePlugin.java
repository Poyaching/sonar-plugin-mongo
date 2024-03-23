/*
 * Example Plugin for SonarQube
 * Copyright (C) 2009-2020 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarsource.plugins.example;

import org.sonar.api.Plugin;
import org.sonarsource.plugins.example.hooks.DisplayQualityGateStatus;
import org.sonarsource.plugins.example.hooks.PostJobInScanner;

/**
 * This class is the entry point for all extensions. It is referenced in
 * pom.xml.
 */
public class ExamplePlugin implements Plugin {

	@Override
	public void define(Context context) {
		// tutorial on hooks
		context.addExtensions(PostJobInScanner.class, DisplayQualityGateStatus.class);

//    // tutorial on languages
//    // https://docs.sonarqube.org/9.4/extend/new-languages/
//    context.addExtensions(FooLanguage.class, FooQualityProfile.class);
//    context.addExtensions(FooLanguageProperties.getProperties());
//
//    // tutorial on measures
//		context.addExtensions(ExampleMetrics.class, SetSizeOnFilesSensor.class, ComputeSizeAverage.class,
//				ComputeSizeRating.class);
//		context.addExtensions(SetSizeOnFilesSensor.class, ComputeSizeAverage.class);
//
//    // tutorial on rules
//    context.addExtensions(JavaRulesDefinition.class, CreateIssuesOnJavaFilesSensor.class);
//    context.addExtensions(FlagRuleDefinition.class, FlagLineSensor.class, FooLintIssuesLoaderSensor.class);
//
//    // tutorial on settings
//    context
//      .addExtensions(HelloWorldProperties.getProperties())
//      .addExtension(SayHelloFromScanner.class);
//
		// tutorial on web extensions
//		context.addExtension(MyPluginPageDefinition.class);
	}
}
