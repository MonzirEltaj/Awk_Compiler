# Awk Compiler in Java

This project is a from-scratch implementation of a  AWK like language in Java. It includes a complete pipeline:

- Lexer (scanner)
- Parser that builds an abstract syntax tree (AST)
- Interpreter that executes AWK style programs on text input

It was written as a learning project to understand how compilers and interpreters work under the hood.



## Features

- Tokenizer for AWK style programs  
  - identifiers, numbers, strings  
  - operators and delimiters  
  - pattern tokens and keywords

- Recursive descent style parser  
  - parses pattern { action } rules  
  - builds an AST representation of the program  
  - performs basic syntax checking and error reporting

- Tree walking interpreter  
  - evaluates expressions and assignments  
  - supports variables and simple arithmetic  
  - executes print style statements and actions for each input line  
  - reads from a text file or standard input (depending on how you call `main`)

