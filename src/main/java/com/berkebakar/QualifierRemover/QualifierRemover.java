package com.berkebakar.QualifierRemover;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;

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

            IDocument document = new Document(sourceCode);
            QualifierRemoverVisitor visitor = new QualifierRemoverVisitor();
            typeDeclaration.accept(visitor);


            TextEdit rootEdit = new MultiTextEdit();

            for (Map.Entry<ASTNode, TextEdit> entry : visitor.getTextEdits().entrySet()) {
                rootEdit.addChild(entry.getValue());
            }
            rootEdit.apply(document);

            String modifiedSource = document.get();

            //Write the modified version to file
            Path outputFilePath = outputPath.resolve(inputPath.getFileName().toString());
            Files.write(outputFilePath, modifiedSource.getBytes());

        } catch (IOException e) {
            System.err.println("An error occurred while writing to file: " + e.getMessage());
        } catch (BadLocationException e){
            System.err.println("An error occurred while modifying source code: " + e.getMessage());
        }
    }
}
