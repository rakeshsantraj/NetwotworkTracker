# Step 1: Build
FROM node:18 AS build
WORKDIR /app
COPY package*.json ./

# Install deps cleanly
RUN npm install
# Add fix for ajv / ajv-keywords mismatch
RUN npm install ajv@^6.12.6 ajv-keywords@^3.5.2 --save-dev

COPY . .
RUN npm run build

# Step 2: Serve with Nginx
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
