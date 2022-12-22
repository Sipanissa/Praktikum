package semanticAnalysis.types;

import parser.AST.AstVisitor;
import parser.AST.ComponentDefinitionNode;
import parser.AST.GameObjectDefinitionNode;
import parser.AST.Node;
import semanticAnalysis.Symbol;
import semanticAnalysis.SymbolTable;

public class TypeBinder implements AstVisitor<Object> {

    private SymbolTable symbolTable;
    private StringBuilder errorStringBuilder;

    /**
     * Create new types for all game object definitions
     *
     * @param symbolTable the symbol table in which to store the types
     * @param rootNode the root node of the program to scan for types
     * @param errorStringBuilder a string builder to which errors will be appended
     */
    public void bindTypes(
            SymbolTable symbolTable, Node rootNode, StringBuilder errorStringBuilder) {
        this.symbolTable = symbolTable;
        this.errorStringBuilder = errorStringBuilder;
        visitChildren(rootNode);
    }

    private Symbol resolveGlobal(String name) {
        return this.symbolTable.getGlobalScope().resolve(name);
    }

    @Override
    public Object visit(GameObjectDefinitionNode node) {
        // create new type with name of definition node
        var newTypeName = node.getIdName();
        if (resolveGlobal(newTypeName) != Symbol.NULL) {
            // TODO: reference file and location of definition
            this.errorStringBuilder.append(
                    "Symbol with name '" + newTypeName + "' already defined");
            // TODO: return explicit null-Type?
            return null;
        }
        var newType = new AggregateType(newTypeName, this.symbolTable.getGlobalScope());
        symbolTable.addSymbolNodeRelation(newType, node);

        // visit all component definitions and get type and create new symbol in gameObject type
        for (var componentDef : node.getComponentDefinitionNodes()) {
            assert componentDef.type == Node.Type.ComponentDefinition;
            var compDefNode = (ComponentDefinitionNode)componentDef;

            var componentType = componentDef.accept(this);
            if (componentType != null) {
                String componentName = compDefNode.getIdName();
                var memberSymbol = new Symbol(componentName, newType, (IType) componentType);
                newType.bind(memberSymbol);
                symbolTable.addSymbolNodeRelation(memberSymbol, compDefNode);
            }
        }

        this.symbolTable.getGlobalScope().bind(newType);
        return newType;
    }

    @Override
    public Object visit(ComponentDefinitionNode node) {
        // resolve components name in global scope
        var componentName = node.getIdName();
        var typeSymbol = resolveGlobal(componentName);
        if (typeSymbol.equals(Symbol.NULL)) {
            this.errorStringBuilder.append(
                    "Could not resolve component name '" + componentName + "'");
            // TODO: return explicit null-Type?
            return null;
        }
        if (!(typeSymbol instanceof IType)) {
            this.errorStringBuilder.append("Symbol '" + componentName + "' is no type!");
            // TODO: return explicit null-Type?
            return null;
        }
        return typeSymbol;
    }
}
