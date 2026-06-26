FROM node:20 AS build
WORKDIR /app
COPY . .
RUN npm install && npm run build

FROM nginx:stable-trixie
COPY --from=build /app/dist/frontend/browser/ /usr/share/nginx/html
