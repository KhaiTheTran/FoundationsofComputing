public class ASTNode {
    private boolean isTerminal;
    private String ruleName;
    private ASTNode[] children;

    private ASTNode(String name, ASTNode[] children, boolean isTerminal) {
        this.ruleName = name;
        this.children = children;
        this.isTerminal = isTerminal;
        if (children != null && children.length == 1 && children[0].isTerminal()) {
            this.children = null;
            this.ruleName = children[0].getValue();
            this.isTerminal = true;
        }
    }

    public ASTNode(String name, ASTNode[] children) {
        this(name, children, false);
    }

    public ASTNode(String name) {
        this(name, null, true);
    }

    public String getRuleName() {
        if (this.isTerminal) {
            return null;
        }
        return this.ruleName;
    }

    public String getValue() {
        if (!this.isTerminal) {
            return this.toString();
        }
        return this.ruleName;
    }

    public ASTNode[] getChildren() {
        return this.children;
    }

    public ASTNode getChild() {
        return this.children[0];
    }

    public ASTNode getLeftChild() {
        return this.children[0];
    }

    public ASTNode getRightChild() {
        return this.children[this.children.length - 1];
    }

    public boolean hasOneChild() {
        return this.children.length == 1;
    }

    public boolean isBinaryOperation() {
        return this.children.length == 3;
    }

    public String toString() {
        if (this.isTerminal()) {
            return this.getValue();
        }
        else {
            String s = "";
            for (ASTNode child : this.getChildren()) {
                s += child.toString();
            }
            return s;
        }
    }

    public boolean isTerminal() {
        return this.isTerminal;
    }
}
