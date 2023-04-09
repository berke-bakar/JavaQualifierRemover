package com.berkebakar.QualifierRemover;

import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import org.eclipse.jdt.core.dom.*;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

public class QualifierRemoverVisitor extends ASTVisitor {

    private final MutableGraph graph;
    public QualifierRemoverVisitor() {
        super(false);
        this.graph = mutGraph("AST").setDirected(true);
    }

    public MutableGraph getGraph() {
        return graph;
    }

    private void addNode(ASTNode node) {
        graph.add(mutNode(Integer.toString(node.hashCode()))
                .add(Label.of(node.getClass().getSimpleName() + "\n" + node.toString()))
        );
    }

    @Override
    public boolean visit(QualifiedName node) {
        addNode(node);
        addEdge(getParentInGraph(node), node);
//
        return true;
    }
    @Override
    public boolean visit(SimpleName node){
        addNode(node);
        addEdge(getParentInGraph(node), node);
        return true;
    }

    private ASTNode getParentInGraph(ASTNode node) {
        ASTNode currentParentNode = node.getParent();

        while (currentParentNode != null) {

            for (MutableNode currentNode : graph.nodes()) {
                if (currentNode.name().equals(Label.of(Integer.toString(currentParentNode.hashCode())))) {
                    return currentParentNode;
                }
            }
            currentParentNode = currentParentNode.getParent();
        }

        return null;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        if (node.getParent() != node.getRoot()) { // Do not add the root type declaration
            addNode(node);
            addEdge(getParentInGraph(node), node);
        }
        return true;
    }

    private void addEdge(ASTNode source, ASTNode target) {
        if (source != null) // no need to add edge if there is no parent
            graph.add(mutNode(Integer.toString(source.hashCode())).addLink(mutNode(Integer.toString(target.hashCode()))));
    }
}
