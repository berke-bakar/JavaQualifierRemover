package com.berkebakar.QualifierRemover;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class QualifierRemover {

    public static void removeQualifiers(Path inputPath, Path outputPath) {
        try {
            String sourceCode = Files.readString(inputPath);
            ASTParser parser = ASTParser.newParser(AST.JLS19);
            parser.setSource(sourceCode.toCharArray());

            Map<String, String> options = JavaCore.getOptions();
            options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_17);
            options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_17);
            options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_17);
            parser.setCompilerOptions(options);
            parser.setResolveBindings(true);
            parser.setBindingsRecovery(true);
            parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);

            TypeDeclaration typeDeclaration = (TypeDeclaration) parser.createAST(null);
            QualifierRemoverVisitor visitor = new QualifierRemoverVisitor();
            typeDeclaration.accept(visitor);



//            String modifiedMethodCode = typeDeclaration.getMethods()[0].toString();
//
//            Map<String, String> formatterOptions = DefaultCodeFormatterConstants.getJavaConventionsSettings();
//            formatterOptions.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, JavaCore.SPACE);
//            formatterOptions.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
//            CodeFormatter codeFormatter = ToolFactory.createCodeFormatter(options);
//            TextEdit textEdit = codeFormatter.format(
//                    CodeFormatter.K_CLASS_BODY_DECLARATIONS, // format a compilation unit
//                    modifiedMethodCode, // source to format
//                    0, // starting position
//                    modifiedMethodCode.length(), // length
//                    0, // initial indentation
//                    System.getProperty("line.separator") // line separator
//            );
//
//            // Get the formatted code from the TextEdit object
//            Document document = new Document(modifiedMethodCode);
//            textEdit.apply(document);
//            String formattedCode = document.get();

            // Write to file the formatted code
//            Path outputFilePath = outputPath.resolve(inputPath.getFileName().toString());
//            File outputFile;
//            if (Files.notExists(outputFilePath)) {
//                outputFile = Files.createFile(outputFilePath).toFile();
//            } else {
//                outputFile = outputFilePath.toFile();
//            }
            Path outputFilePath = outputPath.resolve(inputPath.getFileName().toString().replace(".java", ".png"));
            File outputFile;
            if (Files.notExists(outputFilePath)) {
                outputFile = Files.createFile(outputFilePath).toFile();
            } else {
                outputFile = outputFilePath.toFile();
            }

            MutableGraph astGraph = visitor.getGraph();
            Graphviz.fromGraph(astGraph)
                    .width(3840)
                    .height(2160)
                    .render(Format.PNG).toFile(outputFile);

//            Files.write(outputFilePath, formattedCode.getBytes());


        } catch (IOException e) {
            System.err.println("An error occurred while writing to file: " + e.getMessage());
        }
//        catch (BadLocationException e) {
//            throw new RuntimeException(e);
//        }
    }
}
