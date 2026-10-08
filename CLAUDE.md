﻿# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a PaperMC plugin for Minecraft. It is a Kotlin/Java project (MIT licensed) meant to enhance a SMP server to
play more like an MMO. There is heavy inspiration from Hypixel Skyblock.

## Build

The project is powered by Gradle.

## Architecture

```
docs                     Documentation for server owners/admins

resourcepack             Minecraft resource pack that ships with plugin

src/main
├── java/                Source code for the plugin
└── resources/           Contains builtin data pack and plugin configs

tools                    Various utilities not included with actual plugin code
```

## Coding Standards

This project enforces professional-grade code quality. All contributions must follow these conventions.
Check CONTRIBUTING.md for human-oriented contribution standards as a foundation.

### No Magic Values

- **No magic numbers.** Define named constants for any literal number that isn't immediately self-documenting (0, 1, and simple booleans are fine in context).
- **No magic strings.** Use Enums, constants, or config values instead of raw string literals for keys, identifiers, or categories.
- **No magic HashMap lookups.** Use utility classes, raw dictionaries for structured data. Access fields via attributes, not string keys.

### OOP & Architecture

- Follow single-responsibility — each class and module should have one clear purpose.
- Prefer composition over inheritance unless the framework requires it.
- Keep public APIs small: utilize the protected and private keywords.
- Enums for any finite set of related constants.
- Stay away from duplicate code. Use utilities/static helpers if you must have duplicate logic or messages in two separate places.

### Code Clarity

- Write self-documenting code with descriptive names. Avoid abbreviations unless they are universally understood (e.g., `db`, `ctx`, `msg`).
- Keep functions short and focused. If a function needs a comment explaining a block, that block is a candidate for extraction.
- No commented-out code. Remove dead code entirely.
- No bare `catch` — always catch specific exception types.
- Use early returns to reduce nesting.

### PyCharm / IDE Compatibility

- Code must produce zero warnings in IntelliJ with default inspections enabled.
- No unused imports, variables, or parameters.
- No shadowing of built-in names or outer scope variables.
- No unresolved references — ensure all type stubs and dependencies are available.