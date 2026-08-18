# Contributing

Create a focused branch, add deterministic tests for both allowed and rejected paths, and run:

```bash
./mvnw verify
```

Keep policies small, typed, and free of provider-specific assumptions. Public API changes require documentation and changelog updates. Never commit credentials, tool payloads containing sensitive data, build output, or IDE metadata.
