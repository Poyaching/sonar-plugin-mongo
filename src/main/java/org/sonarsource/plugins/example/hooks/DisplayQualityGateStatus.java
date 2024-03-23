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
package org.sonarsource.plugins.example.hooks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonarqube.ws.Issues.Issue;
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.WsClientFactories;
import org.sonarqube.ws.client.issues.SearchRequest;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;

/**
 * Logs the Quality gate status in Compute Engine when analysis is finished
 * (browse Administration > Projects > Background Tasks). A real use-case would
 * be to send an email or to notify an IRC channel.
 */
public class DisplayQualityGateStatus implements PostProjectAnalysisTask {
	@Override
	public void finished(ProjectAnalysis analysis) {
		var ws = WsClientFactories.getDefault().newClient(
				HttpConnector.newBuilder().url("http://localhost:9000").credentials("admin", "123456").build());
		var ps = 10;
		SearchRequest sr = new SearchRequest();
		sr.setProjects(Arrays.asList(analysis.getProject().getName()));
		sr.setP("1");
		sr.setPs(String.valueOf(ps));
		var code = ws.issues().search(sr);

		List<WriteModel<Document>> list = new ArrayList<>();
		
		// Replace the placeholder with your MongoDB deployment's connection string
		String uri = "mongodb://localhost:27017";
		try (MongoClient mongoClient = MongoClients.create(uri)) {
			MongoDatabase database = mongoClient.getDatabase("sonar");
			MongoCollection<Document> collection = database.getCollection("sonar");

			for (Issue iterable_element : code.getIssuesList()) {
				var carAsString = JsonFormat.printer().print(iterable_element);
				
				var doc = Document.parse(carAsString);
				doc.append("_id", doc.get("key"));
				
				var update = new UpdateOneModel<Document>(new Document("_id", doc.get("key")),
		                new Document("$set", doc),
		                new UpdateOptions().upsert(true));
				
				list.add(update);
			}

		if (code.getIssuesCount() > ps) {
			for (int i = 2; i < (code.getIssuesCount() / ps) + 1; i++) {
				sr.setP(String.valueOf(i));
				code = ws.issues().search(sr);
				for (Issue iterable_element : code.getIssuesList()) {
					var carAsString = JsonFormat.printer().print(iterable_element);
					var doc = Document.parse(carAsString);
					doc.append("_id", doc.get("key"));
					var update = new UpdateOneModel<Document>(new Document("_id", doc.get("key")),
			                new Document("$set", doc),
			                new UpdateOptions().upsert(true));
					
					list.add(update);
				}
				
			}
		}
		
		

		collection.bulkWrite(list);
		
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String getDescription() {
		return "Display Quality Gate status";
	}
}
