# Stage 1: Build the frontend (Vue.js / Vite)
# We use a Node.js 20 image (matching your package.json) to install dependencies and build.
FROM node:20-alpine AS build

# Set the working directory
WORKDIR /app

# Copy package.json and package-lock.json first 
# Docker caches the npm install step unless these files change.
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy the rest of the source code
COPY . .

# Build the project into the "dist" folder
RUN npm run build

# Stage 2: Serve with Nginx
# We use a lightweight Nginx image to serve the static HTML/JS/CSS files built in Stage 1.
FROM nginx:alpine

# Copy the built files from Stage 1 into Nginx's public html folder
COPY --from=build /app/dist /usr/share/nginx/html

# Copy our custom Nginx configuration to handle Vue Router
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80 (default HTTP port)
EXPOSE 80

# Start Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]
