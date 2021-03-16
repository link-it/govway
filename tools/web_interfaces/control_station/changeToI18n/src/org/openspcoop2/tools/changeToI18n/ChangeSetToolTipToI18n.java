package org.openspcoop2.tools.changeToI18n;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;


public class ChangeSetToolTipToI18n {
	private static final String SET_TOOL_TIP = "setToolTip(";
	private static final String MESSAGE_FORMAT = "MessageFormat.format(";

	private static String resolveProperty( Class<?> cls, String costanteName ) throws Exception {
		String res = null;
		try {
			Field field = cls.getField( costanteName );
			if ( field.getType().equals( String.class ) )
				res = (String)field.get(null);
			if ( res.endsWith( " " ) )
				res = res.substring( 0, res.length() - 1 ) + "&nbsp;";
		} catch ( Throwable t ) {
			System.out.println( "Warning: NoSuckField: " + costanteName );
		}
		return res;
	}

	private static String toProperCase( String s ) {
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	private static String toCamelCase( String s ) {
		String[] parts = s.split("_");
		String camelCaseString = "";
		for ( String part : parts )
			camelCaseString = camelCaseString + toProperCase(part);
		return camelCaseString;
	}

	private static String buildI18nPropName( String costanteClassName, String costanteName ) {
		String prefixClasse = costanteClassName.substring( 0, costanteClassName.indexOf( "Costanti" ) ) +
							  costanteClassName.substring( costanteClassName.indexOf( "Costanti" ) + "Costanti".length() );
		if ( prefixClasse.length() == 0 )
			prefixClasse = "Govway";
		String constName = costanteName;
		if ( constName.startsWith( "LABEL_" ) )
			constName = constName.substring( "LABEL_".length() );
		if ( constName.indexOf( "LABEL_" ) > 0 ) {
			int chrIx = constName.indexOf( "LABEL_" );
			constName = constName.substring( 0, chrIx ) + constName.substring( chrIx + "LABEL_".length() );
		}
		if ( constName.equals( costanteName ) && constName.startsWith( "PARAMETRO_" ) )
			constName = constName.substring( "PARAMETRO_".length() );
		if ( constName.equals( costanteName ) && constName.startsWith( "PARAMETER_" ) )
			constName = constName.substring( "PARAMETER_".length() );
		if ( constName.equals( costanteName ) ) {
			System.out.println( "Warning: Unknown constant prefix: " + costanteName );
			char firstChar = constName.charAt(0);
			if ( firstChar < 'A' || firstChar > 'Z' )
				return null;
		}
		constName = toCamelCase( constName );
		return prefixClasse + "." + constName;
	}

	private static String changeSetToolTipRow( String inLine, String costanteClassName, String costanteName, String fullClassName, Properties tooltipProperties ) throws Exception {
		Class<?> cls = null;
		try {
			char firstChar = costanteClassName.charAt(0);
			if ( firstChar < 'A' || firstChar > 'Z' ) {
				System.out.println( "Warning: Not class name: " + costanteClassName );
				return inLine;
			}
			cls = Class.forName( fullClassName );
			if ( cls == null )
				throw new RuntimeException( "Unknown imported class of: " + costanteClassName );
		} catch ( Throwable t ) {
			t.printStackTrace();
			return inLine;
		}

		String i18nPropName = buildI18nPropName( costanteClassName, costanteName );
		if ( i18nPropName == null )
			return inLine;
		String propValue = resolveProperty( cls, costanteName );
		if ( propValue == null )
			return inLine;
		String storedPropValue = (String)tooltipProperties.get( i18nPropName );
		if ( storedPropValue != null ) {
			if ( !storedPropValue.equals( propValue ) )
				throw new RuntimeException( "Costante non compatible - " + costanteName + " - " + i18nPropName + " : " + propValue + " vs " + storedPropValue );
		} else {
			tooltipProperties.put( i18nPropName, propValue );
		}
		System.out.println( "Costante: " + propValue );
		int setToolTipPosIx = inLine.indexOf( SET_TOOL_TIP );
		int endPosIx = inLine.indexOf( ");", setToolTipPosIx );
		int msgFormatPosIx = inLine.indexOf( MESSAGE_FORMAT, setToolTipPosIx );

		StringBuilder resBuff = new StringBuilder( inLine.length() );
		resBuff.append( inLine.substring( 0, setToolTipPosIx + SET_TOOL_TIP.length() ) );
		resBuff.append( " ServletUtils.getToolTipFromResourceBundle( session, " );
		if ( msgFormatPosIx >= 0 ) {
			int endMsgFormatPosIx = inLine.indexOf( ",", msgFormatPosIx + MESSAGE_FORMAT.length() );
			endPosIx = inLine.indexOf( ");", endMsgFormatPosIx + 1 );
			resBuff.append( "\"" ).append( i18nPropName ).append( "\"" );
			resBuff.append( inLine.substring( endMsgFormatPosIx ) );
		} else {
			resBuff.append( "\"" ).append( i18nPropName ).append( "\" ) " );
			resBuff.append( inLine.substring( endPosIx ) );
		}
		return resBuff.toString();
	}

	private static void execChangeSetToolTip( String inputDir, String filePath, String outputDir, String propFileName ) {
		File outFD = null;
		try {
			Path inPath = Paths.get( inputDir, filePath );
			Path outPath = Paths.get( outputDir, filePath );
			Path propPath = Paths.get( outputDir, propFileName );
			Properties tooltipProperties = new Properties();
			if ( propPath.toFile().exists() ) {
				FileInputStream propIs = new FileInputStream( propPath.toFile() );
				tooltipProperties.load( propIs );
				propIs.close();
			}
			if ( inPath.toFile().exists() ) {
				Path outDirPath = outPath.getParent();
				if ( outDirPath.toFile().exists() || outDirPath.toFile().mkdirs() ) {
					System.out.println( "Start change of: " + inPath.getFileName() );
					outFD = outPath.toFile();
					try ( BufferedReader is = new BufferedReader( new FileReader( inPath.toFile() ) );
						  BufferedWriter os = new BufferedWriter( new FileWriter( outPath.toFile() ) ) ) {
						List<String> importedClasses = new ArrayList<String>();
						boolean importServletUtilsFlag = false;
						String classPackage = null;
						String inLine;
						String outLine;
						while ( ( inLine = is.readLine() ) != null ) {
							if ( inLine.startsWith( "package " ) ) {
								int endIx = inLine.indexOf( ";" );
								classPackage = inLine.substring( "package".length(), endIx ).trim();
							}
							if ( inLine.startsWith( "import " ) ) {
								int endIx = inLine.indexOf( ";" );
								String curImportedClass = inLine.substring( "import".length(), endIx ).trim();
								if ( !importedClasses.contains( curImportedClass ) )
									importedClasses.add( curImportedClass );
								if ( curImportedClass.startsWith( "org.openspcoop2.web.lib.mvc" ) ) {
									if ( !importServletUtilsFlag ) {
										if ( !curImportedClass.equals( "org.openspcoop2.web.lib.mvc.ServletUtils" ) ) {
											os.append( "import org.openspcoop2.web.lib.mvc.ServletUtils;" );
											os.newLine();
											importServletUtilsFlag = true;
										}
										importServletUtilsFlag = true;
									} else {
										if ( curImportedClass.equals( "org.openspcoop2.web.lib.mvc.ServletUtils" ) )
											continue;
									}
								}
							}

							boolean commentFlag = inLine.trim().startsWith( "//" );
							int setToolTipPosIx = inLine.indexOf( SET_TOOL_TIP );
							int costantiPosIx = inLine.indexOf( "Costanti" );
							if ( !commentFlag && setToolTipPosIx > 0 && costantiPosIx > setToolTipPosIx ) {
								System.out.println( "found change to be execute: " + inLine );
								int endSetToolTipPosIx = inLine.indexOf( ")", costantiPosIx );
								if ( endSetToolTipPosIx > 0 ) {
									String costanteDef = inLine.substring( setToolTipPosIx + SET_TOOL_TIP.length(), endSetToolTipPosIx ).trim();
									System.out.println( "Costante da sostituire: " + costanteDef );
									if ( costanteDef.indexOf( MESSAGE_FORMAT ) == 0 ) {
										String formatDef = inLine.substring( setToolTipPosIx + SET_TOOL_TIP.length(), endSetToolTipPosIx  + 1 ).trim();
										System.out.println( "Found MessageFormat: " + formatDef );
										int endFirstParamPosIx = formatDef.indexOf( "," );
										costanteDef = formatDef.substring( MESSAGE_FORMAT.length(), endFirstParamPosIx ).trim();
										System.out.println( "Costante interna da sostituire: " + costanteDef );

										if ( costanteDef.indexOf( "\"" ) < 0 ) {
											String costanteClassName = costanteDef.substring( 0, costanteDef.lastIndexOf( "." ) );
											String costanteName = costanteDef.substring( costanteDef.lastIndexOf( "." ) + 1 );
											System.out.println( "Costante: " + costanteClassName + " - " + costanteName );
											if ( costanteClassName.indexOf( "." ) > 0 ) {
												String simpleClassName = costanteClassName.substring( costanteClassName.lastIndexOf( "." ) );
												outLine = changeSetToolTipRow( inLine, simpleClassName, costanteName, costanteClassName, tooltipProperties );
											} else {
												Optional<String> foundClassName = importedClasses.stream().filter( (n) -> n.endsWith( "." + costanteClassName ) ).findFirst();
												if ( foundClassName.isPresent() ) {
													outLine = changeSetToolTipRow( inLine, costanteClassName, costanteName, foundClassName.get(), tooltipProperties );
													outLine += "\t//" + costanteDef;
												} else
												if ( classPackage != null ) {
													outLine = changeSetToolTipRow( inLine, costanteClassName, costanteName, classPackage + "." + costanteClassName, tooltipProperties );
													outLine += "\t//" + costanteDef;
												} else
													throw new RuntimeException( "Fail to find imported class of: " + costanteClassName );
											}
										} else
											outLine = inLine;
									} else
									if ( costanteDef.indexOf( "\"" ) < 0 ) {
										String costanteClassName = costanteDef.substring( 0, costanteDef.lastIndexOf( "." ) );
										String costanteName = costanteDef.substring( costanteDef.lastIndexOf( "." ) + 1 );
										System.out.println( "Costante: " + costanteClassName + " - " + costanteName );
										if ( costanteClassName.indexOf( "." ) > 0 ) {
											String simpleClassName = costanteClassName.substring( costanteClassName.lastIndexOf( "." ) );
											outLine = changeSetToolTipRow( inLine, simpleClassName, costanteName, costanteClassName, tooltipProperties );
										} else {
											Optional<String> foundClassName = importedClasses.stream().filter( (n) -> n.endsWith( "." + costanteClassName ) ).findFirst();
											if ( foundClassName.isPresent() ) {
												outLine = changeSetToolTipRow( inLine, costanteClassName, costanteName, foundClassName.get(), tooltipProperties );
												outLine += "\t//" + costanteDef;
											} else
											if ( classPackage != null ) {
												outLine = changeSetToolTipRow( inLine, costanteClassName, costanteName, classPackage + "." + costanteClassName, tooltipProperties );
												outLine += "\t//" + costanteDef;
											} else
												throw new RuntimeException( "Fail to find imported class of: " + costanteClassName );
										}
									} else
										outLine = inLine;
								} else
									outLine = inLine;
							} else
								outLine = inLine;
							os.append( outLine );
							os.newLine();
							os.flush();
						}
					}
					FileOutputStream propOs = new FileOutputStream( propPath.toFile() );
					tooltipProperties.store( propOs, "ToolTip constants" );
				}
			} else
				throw new RuntimeException( "File inesistente: " + filePath );
		} catch ( Throwable t ) {
			t.printStackTrace();
			if ( outFD !=  null )
				outFD.delete();
		}
	}

	public static void main( String[] args ) throws Exception {
		String inputDir = "src";
		String outputDir = "dest";
		String propFileName = "toolTips_it.properties";

		for ( int arg_ix = 0; arg_ix < args.length; arg_ix++ ) {
			if ( args[ arg_ix ].equals( "-s" ) ) {
				inputDir = args[ ++arg_ix ];
			} else
			if ( args[ arg_ix ].equals( "-o" ) ) {
				outputDir = args[ ++arg_ix ];
			} else
			if ( args[ arg_ix ].equals( "-p" ) ) {
				propFileName = args[ ++arg_ix ];
			} else
			if ( args[ arg_ix ].equals( "-l" ) ) {
				String logFileName = args[ ++arg_ix ];
				System.setOut( new PrintStream( logFileName ) );
			} else
			if ( args[ arg_ix ].equals( "-e" ) ) {
				String logFileName = args[ ++arg_ix ];
				System.setErr( new PrintStream( logFileName ) );
			} else
			if ( args[ arg_ix ].equals( "-f" ) ) {
				String srcFileNameList = args[ ++arg_ix ];
				String srcDir = inputDir;
				String destDir = outputDir;
				String tooltipsPropFile = propFileName;
				try {
					Files.readAllLines( Paths.get( srcFileNameList ) ).forEach( (fileName) -> execChangeSetToolTip( srcDir, fileName, destDir, tooltipsPropFile ) );
				} catch ( Throwable t ) {
					t.printStackTrace();
				}
			} else {
				String curFilePath = args[ arg_ix ];
				execChangeSetToolTip( inputDir, curFilePath, outputDir, propFileName );
			}
		}
	}
}
