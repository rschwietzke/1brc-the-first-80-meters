# Agent Instructions

Please adhere to the following rules when contributing to this repository:

## General Behavior
- **Explicit Confirmation:** Do not implement anything unless told and confirmed by the user.
- **Think before coding:** State your assumptions out loud. If the request is ambiguous, ask. If a simpler approach exists, push back. Stop when you are confused, name what is unclear, do not just pick one interpretation and run.
- **Simplicity first:** Write the minimum code that solves the problem. No speculative abstractions. No flexibility nobody asked for. The test: would a senior engineer call this overcomplicated.
- **Surgical changes:** Touch only what the task requires. Do not improve neighboring code. Do not refactor what is not broken. Every changed line should trace back to the request.
- **Goal-driven execution:** Turn vague instructions into verifiable targets before writing a line. "Add validation" becomes "write tests for invalid inputs, then make them pass".

## Coding Standards
- **Test-Driven Development (TDD):** Write unit and integration tests before implementing new functionality. Ensure comprehensive test coverage for all new code.
- **Code Style:** Prefer the Allman code style (braces on a new line).
- **Braces:** Always use braces `{}` for `if`, `for`, `while`, and `do` bodies, even for single-line statements.
- **Documentation:** Always comment code thoroughly.
- **AI Attribution:** If a file is exclusively created by AI, mark it in the class comment accordingly with the specific model you used.
- **License Headers:** Always add an Apache license header to all new source code files.
- **Final:** Prefer final for methods and fields in new code only.
- **Java Language Features:** Use JDK 21 features and syntax where appropriate.
- **this:** Always use `this` for method and field references.

## Third-Party Dependencies
- **Attribution:** If you use an open-source library, document it in `NOTICE.md` and include the appropriate license references inside `doc/3rd-party-licenses`.

## GIT
- **Merge:** Never fast-forward, never stash.
- **Stashing:** Ask for permission every time.

## Testing & Specifications
- **Manual & Automated Tests:** You MUST create test cases as part of any specification process or implementation work.
- **Defect Resolution:** When fixing a bug, you MUST write a test case first to reproduce the defect, and implement the fix afterwards.
- **Maintenance:** You MUST review existing test cases and update them to reflect any logic, UI, or specification changes made during implementation.
