/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openspcoop2.ant.git;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryCache;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.FS;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;

/**
 * Classe utilizzata per costruire una Busta eGov, o parti di essa.
 *
 *
 * @author Manca Andrea (amanca@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GitStatus extends Task{

	static{
		try{
			setDefaultConsoleLogConfiguration(Level.ERROR);
		}catch(Exception e){
			e.printStackTrace(System.err);
		}
	}

	private static final String LAYOUT = "%p <%d{dd-MM-yyyy HH:mm:ss.SSS}> %C.%M(%L): %m %n %n";
	
	private static void setDefaultConsoleLogConfiguration(Level level) {
		setDefaultConsoleLogConfiguration(level, LAYOUT);
	}
	private static void setDefaultConsoleLogConfiguration(Level level,
			String layout) {
		setDefaultLogConfiguration(level, true, layout, null, null);
	}
	private static void setDefaultLogConfiguration(Level level,boolean console,String layoutConsole,
			File file,String layoutFile) {

		if(layoutConsole==null){
			layoutConsole=LAYOUT;
		}
		if(layoutFile==null){
			layoutFile=LAYOUT;
		}

		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		builder.setConfigurationName("ConsoleDefault");
		builder.setStatusLevel(Level.ERROR);
		// Console Appender
		if(console){
			AppenderComponentBuilder appenderBuilder = builder.newAppender("Stdout", "CONSOLE").
					addAttribute("target",  ConsoleAppender.Target.SYSTEM_OUT);
			appenderBuilder.add(builder.newLayout("PatternLayout")
					.addAttribute("pattern", layoutConsole));
			builder.add(appenderBuilder);
		}
		if(file!=null){
			// create a rolling file appender
			LayoutComponentBuilder layoutBuilder = builder.newLayout("PatternLayout")
					.addAttribute("pattern", layoutFile);
			ComponentBuilder<?> triggeringPolicy = builder.newComponent("Policies")
					.addComponent(builder.newComponent("CronTriggeringPolicy").addAttribute("schedule", "0 0 0 * * ?"))
					.addComponent(builder.newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "100M"));
			AppenderComponentBuilder appenderBuilder = builder.newAppender("rolling", "RollingFile")
					.addAttribute("fileName", file.getAbsolutePath())
					.addAttribute("filePattern", file.getAbsolutePath()+".%i")
					.add(layoutBuilder)
					.addComponent(triggeringPolicy);
			builder.add(appenderBuilder);
		}
		// RootLogger
		RootLoggerComponentBuilder rootLoggerBuilder = builder.newRootLogger(level);
		if(console){
			rootLoggerBuilder.add(builder.newAppenderRef("Stdout"));
		}
		if(file!=null){
			rootLoggerBuilder.add(builder.newAppenderRef("rolling"));
		}
		builder.add(rootLoggerBuilder);
		// Initialize
		BuiltConfiguration bCon = builder.build();
		org.apache.logging.log4j.core.config.Configurator.initialize(bCon);
	}

	private File src;
	private String path;
	private String dateformat;


	private String committerMailProperty = null;
	private String committerNameProperty = null;
	private String committerCommitDateProperty = null;


	private String authorMailProperty = null;
	private String authorNameProperty = null;
	private String authorCommitDateProperty = null;

	private String hashProperty = null;
	private String shortHashProperty = null;


	public String getPath() {
		return this.path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public File getSrc() {
		return this.src;
	}

	public void setSrc(File src) {
		this.src = src;
	}


	public String getDateformat() {
		return this.dateformat;
	}


	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}


	/** {@inheritDoc} */
	@Override
	public void execute() throws BuildException {
		if (this.src == null) {
			throw new BuildException("Repository path not specified.");
		}

		SimpleDateFormat chosenformat =null;
		if (getDateformat() !=  null )
			chosenformat = new SimpleDateFormat(getDateformat());
		else
			chosenformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss Z");


		if (!RepositoryCache.FileKey.isGitRepository(new File(this.src, ".git"),
				FS.DETECTED)) {
			throw new BuildException("Specified path (" + this.src
					+ ") is not a git repository.");
		}


		try (Repository repository = new FileRepositoryBuilder().readEnvironment().findGitDir(this.src).build()) {     
			execute(repository, chosenformat);
		} catch (IOException e) {
			throw new BuildException("Could not access repository " + this.src, e);
		}

	}
	private void execute(Repository repository, SimpleDateFormat chosenformat) throws BuildException, RevisionSyntaxException, IOException {
		LogCommand gitLog;
		try (Git git = new Git(repository)) {

			gitLog = git.log();
			ObjectId head = repository.resolve(Constants.HEAD);
			gitLog.add(head);

			if (getPath() != null) {
				gitLog.addPath(this.path);
			}

			Iterable<RevCommit> logs = gitLog.setMaxCount(1).call(); //Recupero solo il primo log
			Project antproject = getProject();
			for (RevCommit rev : logs) {
				execute(antproject, rev, chosenformat);
			}
		} catch (NoHeadException e) {
			throw new BuildException("invalid repository " + this.src, e);
		} catch (GitAPIException e) {
			throw new BuildException("Git access problem " + this.src, e);
		}
	}
	private void execute(Project antproject, RevCommit rev, SimpleDateFormat chosenformat) {
		PersonIdent committer = rev.getCommitterIdent();                	
		PersonIdent author = rev.getAuthorIdent();

		readCommitter(antproject, committer, chosenformat);

		readAuthor(antproject, author, chosenformat);

		if(getHashProperty() != null) {
			String hash = rev.getName(); 
			if (hash != null)
				antproject.setProperty(getHashProperty(), hash);
			else
				antproject.setProperty(getHashProperty(), "-1");
		}

		if(getShortHashProperty() != null) {
			String hash = rev.getName();
			if (hash != null)
				antproject.setProperty(getShortHashProperty(), hash.substring(0, 8));
			else
				antproject.setProperty(getShortHashProperty(), "-1");
		}
	}
	private void readCommitter(Project antproject, PersonIdent committer, SimpleDateFormat chosenformat) {
		if (getCommitterMailProperty() != null ) {
			String committermail = committer.getEmailAddress();
			if (committermail != null )
				antproject.setProperty(getCommitterMailProperty(), committermail);        
			else
				antproject.setProperty(getCommitterMailProperty(), "nomail@dev.null");
		}

		if (getCommitterNameProperty() != null ) {
			String committername = committer.getName();
			if (committername != null )
				antproject.setProperty(getCommitterNameProperty(), committername);
			else
				antproject.setProperty(getCommitterNameProperty(), "");
		}

		if(getCommitterCommitDateProperty() != null) {
			Date committerdate = null;
			if(committer.getWhenAsInstant()!=null) {
				committerdate = Date.from(committer.getWhenAsInstant());
			}
			if (committerdate != null ) 
				antproject.setProperty(getCommitterCommitDateProperty(),chosenformat.format(committerdate));
			else 
				antproject.setProperty(getCommitterCommitDateProperty(),chosenformat.format(new Date()));
		}
	}
	private void readAuthor(Project antproject, PersonIdent author, SimpleDateFormat chosenformat) {
		if (getAuthorMailProperty() != null ) {
			String authormail = author.getEmailAddress();
			if (authormail != null )
				antproject.setProperty(getAuthorMailProperty(),authormail );
			else
				antproject.setProperty(getAuthorMailProperty(), "nomail@dev.null");
		}

		if (getAuthorNameProperty() != null ) {
			String authorname =author.getName();
			if(authorname != null )
				antproject.setProperty(getAuthorNameProperty(), authorname);
			else
				antproject.setProperty(getAuthorNameProperty(), "");
		}

		if(getAuthorCommitDateProperty() != null) {
			Date authordate = null;
			if(author.getWhenAsInstant()!=null) {
				authordate = Date.from(author.getWhenAsInstant());
			}
			if (authordate != null)
				antproject.setProperty(getAuthorCommitDateProperty(), chosenformat.format(authordate));
			else
				antproject.setProperty(getAuthorCommitDateProperty(), chosenformat.format(new Date()));
		}
	}


	public String getCommitterMailProperty() {
		return this.committerMailProperty;
	}


	public void setCommitterMailProperty(String committerMailProperty) {
		this.committerMailProperty = committerMailProperty;
	}


	public String getCommitterNameProperty() {
		return this.committerNameProperty;
	}


	public void setCommitterNameProperty(String committerNameProperty) {
		this.committerNameProperty = committerNameProperty;
	}


	public String getAuthorMailProperty() {
		return this.authorMailProperty;
	}


	public void setAuthorMailProperty(String authorMailProperty) {
		this.authorMailProperty = authorMailProperty;
	}


	public String getAuthorNameProperty() {
		return this.authorNameProperty;
	}


	public void setAuthorNameProperty(String authorNameProperty) {
		this.authorNameProperty = authorNameProperty;
	}


	public String getHashProperty() {
		return this.hashProperty;
	}


	public void setHashProperty(String hashProperty) {
		this.hashProperty = hashProperty;
	}


	public String getShortHashProperty() {
		return this.shortHashProperty;
	}


	public void setShortHashProperty(String shortHashProperty) {
		this.shortHashProperty = shortHashProperty;
	}




	public String getCommitterCommitDateProperty() {
		return this.committerCommitDateProperty;
	}


	public void setCommitterCommitDateProperty(String committerCommitDateProperty) {
		this.committerCommitDateProperty = committerCommitDateProperty;
	}


	public String getAuthorCommitDateProperty() {
		return this.authorCommitDateProperty;
	}


	public void setAuthorCommitDateProperty(String authorCommitDateProperty) {
		this.authorCommitDateProperty = authorCommitDateProperty;
	}


	/**	public Repository openRepository() throws IOException {
	        FileRepositoryBuilder builder = new FileRepositoryBuilder();
	        return builder
	        		.setGitDir(new File(src,".git"))
	                .readEnvironment() // scan environment GIT_* variables
	                .findGitDir() // scan up the file system tree
	                .build();
	    }
	
		public void main(String[] args)  throws IOException, GitAPIException {
	
			
	        try (Repository repository = openRepository()) {
	            try (Git git = new Git(repository)) {
	                Iterable<RevCommit> logs = git.log().call();
	
	                for (RevCommit rev : logs) {
	                	
	                	PersonIdent committer = rev.getCommitterIdent();                	
	                	PersonIdent author = rev.getAuthorIdent();
	                	
	                	
	                	System.out.format("%s : %s [%s] -> %s\n",rev.getName(),
	                			author.getName(),
	                			author.getWhen(),
	                	rev.getShortMessage()
	                	);
	                	
	                	
	                	System.out.format("%s : %s [%s] -> %s\n",rev.getName(),
	                			committer.getName(),
	                			committer.getWhen(),
	                	rev.getShortMessage()
	                	);
	                	break; //solo il primo
	                	
	                }
	            }
	        }
		}*/

}


