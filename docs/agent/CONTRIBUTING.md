# Contributing

## Development rules

- Keep the app offline-first for MVP.
- Do not add surveillance features.
- Do not add ads or analytics.
- Do not request sensitive permissions without documentation.
- Do not use full-lockdown claims.
- Keep parent setup simple.
- Keep child home screen calm and readable.

## Branching

Recommended:

- `main`
- `develop`
- `feature/<short-name>`
- `fix/<short-name>`

## Commit style

Use clear commits:

```text
feat: add parent pin setup
fix: prevent child layout editing without pin
test: add school mode visibility tests
docs: update privacy permissions map
```

## Pull request checklist

- [ ] Build passes.
- [ ] Tests pass or failures documented.
- [ ] Privacy guardrails checked.
- [ ] No unnecessary permissions added.
- [ ] Documentation updated.
- [ ] Evidence report added if applicable.
