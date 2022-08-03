# Install dependencies only when needed
FROM node:18-alpine AS deps
RUN apk add --no-cache libc6-compat
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install

# Rebuild the source code only when needed
FROM node:18-alpine AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .

ARG VITE_API_BASE_URL
ARG VITE_STRIPE_PUBLIC_KEY
RUN npm run build

# Production image, copy all the files and run next
FROM nginx:1.23-alpine AS runner
WORKDIR /app

COPY --from=builder /app/build /usr/share/nginx/html

EXPOSE 80
ENV PORT 80