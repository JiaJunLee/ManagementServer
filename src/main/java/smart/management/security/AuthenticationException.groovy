package smart.management.security

class AuthenticationException extends Exception {
    AuthenticationException(String s) {
        super(s)
    }
    @Override
    String toString() {
        return this.message
    }
}
