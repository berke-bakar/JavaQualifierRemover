package com.berkebakar.QualifierRemover;

import org.eclipse.jdt.core.dom.*;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;

import java.util.HashMap;
import java.util.Map;

public class QualifierRemoverVisitor extends ASTVisitor {
    private final Map<ASTNode, TextEdit> textEdits;
    public QualifierRemoverVisitor() {
        super(false);
        this.textEdits = new HashMap<>();
    }

    public Map<ASTNode, TextEdit> getTextEdits(){
        return textEdits;
    }

    @Override
    public boolean visit(QualifiedName node) {
        // Check if the parent of this node is not a QualifiedName, and left child is not a SimpleName
        if (!(node.getParent() instanceof QualifiedName) && !(node.getQualifier() instanceof SimpleName)) {
            // Get the simple name part of the qualified name
            SimpleName simpleName = node.getName();

            // Create a new node that only contains the simple name
            SimpleName newSimpleName = node.getAST().newSimpleName(simpleName.getIdentifier());

            // Replace the qualified name with the simple name based on parent's type (this way it is more accurate)
            ASTNode parent = node.getParent();
            if (parent instanceof MethodInvocation methodInvocation) {
                textEdits.put(node, new ReplaceEdit(methodInvocation.getStartPosition() + node.getStartPosition() - parent.getStartPosition(), node.getLength(), newSimpleName.getIdentifier()));
            } else if (parent instanceof FieldAccess fieldAccess) {
                textEdits.put(node, new ReplaceEdit(fieldAccess.getStartPosition() + node.getStartPosition() - parent.getStartPosition(), node.getLength(), newSimpleName.getIdentifier()));
            } else if (parent instanceof MarkerAnnotation markerAnnotation) {
                textEdits.put(node, new ReplaceEdit(markerAnnotation.getStartPosition() + node.getStartPosition() - parent.getStartPosition(), node.getLength(), newSimpleName.getIdentifier()));
            } else if (parent instanceof SimpleType simpleType) {
                textEdits.put(node, new ReplaceEdit(simpleType.getStartPosition() + node.getStartPosition() - parent.getStartPosition(), node.getLength(), newSimpleName.getIdentifier()));
            } else if (parent instanceof ReturnStatement returnStatement){
                textEdits.put(node, new ReplaceEdit(returnStatement.getStartPosition() + node.getStartPosition() - parent.getStartPosition(), node.getLength(), newSimpleName.getIdentifier()));
            } else if (parent instanceof NormalAnnotation normalAnnotation){
                textEdits.put(node, new ReplaceEdit(normalAnnotation.getStartPosition() + node.getStartPosition() - parent.getStartPosition(), node.getLength(), newSimpleName.getIdentifier()));
            }
        }

        return true;
    }
}
