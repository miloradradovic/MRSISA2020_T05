package com.project.tim05.security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import com.project.tim05.model.User;

// Utility klasa za rad sa JSON Web Tokenima
@Component
public class TokenUtils {

	// Izdavac tokena
	@Value("tim05")
	private String APP_NAME;

	@Value("trimusketara")
	public String SECRET;

	
	@Value("300000")
	private int EXPIRES_IN;

	// Naziv headera kroz koji ce se prosledjivati JWT u komunikaciji server-klijent
	@Value("Authorization")
	private String AUTH_HEADER;

	// Algoritam za potpisivanje JWT
	private SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;

	
	public String generateToken(String email) {
		return Jwts.builder()
				.setIssuer(APP_NAME)
				.setSubject(email)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(generateExpirationDate())
				// .claim("key", value) //moguce je postavljanje proizvoljnih podataka u telo JWT tokena
				.signWith(SIGNATURE_ALGORITHM, SECRET).compact();
	}

	private Date generateExpirationDate() {
		return new Date(new Date().getTime() + EXPIRES_IN);
	}

	// Funkcija za refresh JWT tokena
	public String refreshToken(String token) {
		String refreshedToken;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			claims.setIssuedAt(new Date());
			refreshedToken = Jwts.builder()
					.setClaims(claims)
					.setExpiration(generateExpirationDate())
					.signWith(SIGNATURE_ALGORITHM, SECRET).compact();
		} catch (Exception e) {
			refreshedToken = null;
		}
		return refreshedToken;
	}

	public boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
		final Date created = this.getIssuedAtDateFromToken(token);
		return (!(this.isCreatedBeforeLastPasswordReset(created, lastPasswordReset))
				&& (!(this.isTokenExpired(token))));
	}

	// Funkcija za validaciju JWT tokena
	public Boolean validateToken(String token, UserDetails userDetails) {
		User user = (User) userDetails;
		final String username = getUsernameFromToken(token);
		final Date created = getIssuedAtDateFromToken(token);
		
		return (username != null && username.equals(userDetails.getUsername())
				&& !isCreatedBeforeLastPasswordReset(created, user.getLastPasswordResetDate()));
	}

	public String getUsernameFromToken(String token) {
		String username;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			username = claims.getSubject();
		} catch (Exception e) {
			username = null;
		}
		return username;
	}

	public Date getIssuedAtDateFromToken(String token) {
		Date issueAt;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			issueAt = claims.getIssuedAt();
		} catch (Exception e) {
			issueAt = null;
		}
		return issueAt;
	}

	public String getAudienceFromToken(String token) {
		String audience;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			audience = claims.getAudience();
		} catch (Exception e) {
			audience = null;
		}
		return audience;
	}

	public Date getExpirationDateFromToken(String token) {
		Date expiration;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			expiration = claims.getExpiration();
		} catch (Exception e) {
			expiration = null;
		}
		return expiration;
	}

	public int getExpiredIn() {
		return EXPIRES_IN;
	}

	// Funkcija za preuzimanje JWT tokena iz zahteva
	public String getToken(HttpServletRequest request) {
		String authHeader = getAuthHeaderFromHeader(request);

		// JWT se prosledjuje kroz header Authorization u formatu:
		// Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}

		return null;
	}

	public String getAuthHeaderFromHeader(HttpServletRequest request) {
		return request.getHeader(AUTH_HEADER);
	}
	
	private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
		return (lastPasswordReset != null && created.before(lastPasswordReset));
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = this.getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	// Funkcija za citanje svih podataka iz JWT tokena
	private Claims getAllClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser()
					.setSigningKey(SECRET)
					.parseClaimsJws(token)
					.getBody();
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}

}