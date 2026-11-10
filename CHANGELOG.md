# Changelog

All notable changes to `resilient-notification-hub` are documented here.

## [1.0.0] - 2026-11-06
### Added
- Multi-channel notification dispatching for Email, SMS, Push, and Webhooks.
- Distributed Redis Token Bucket sliding-window rate limiter per tenant.
- Resilience4j Circuit Breaker configuration with automated fallback provider routing.
- Actuator telemetry exposure for circuit breaker state and health metrics.
- Comprehensive unit tests verifying rate-limit bounds and fallback mechanics.
- GitHub Actions CI workflow for automated testing and package compilation.
